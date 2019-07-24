package us.nyrene.fluidstories;

import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.entity.Player;

enum DErrorType { INVALIDTREE, NULLNODE, NULLNATTR, NOERROR, OUTOFBOUNDS}

class DError {
    String msg;
    DErrorType type;

    public DError(DErrorType givenType, String givenMessage) {
        msg = givenMessage;
        type = givenType;
    }

    public DError() {
        msg = "";
        type = DErrorType.NOERROR;
    }

    void setMsg(String givenMsg) {
        msg = givenMsg;
    }

    void setType(DErrorType givenType) {
        type = givenType;
    }

    void reset() {
        type = DErrorType.NOERROR;
        msg = "";
    }

}

class PlayerStatement {
    String msg;
    NPCStatement npcPrevious;
    NPCStatement npcNode;

    public PlayerStatement(String givenMsg, NPCStatement givenNode) {
        if (givenMsg != null) {
            msg = givenMsg;
        } else {
            System.out.println("Can't add null msg to in PlayerStatement!");
            msg = "";
        }

        if (givenNode != null) {
            npcPrevious = givenNode;
        } else {
            System.out.println("Can't add null NPCStatement in PlayerStatement()");
        }

    }
}

class NPCStatement {
    public static final short MAXPLAYERSTATEMENTS = 5;

    String msg;
    PlayerStatement[] pStatements; // set size later, using MAXPLAYERSTATEMENTS
    int numPStatements = 0;
    PlayerStatement playerPrevious;

    public NPCStatement() {
        msg = "";
        pStatements = new PlayerStatement[MAXPLAYERSTATEMENTS];
    }

    public NPCStatement(String givenMsg) {
        msg = givenMsg;
        pStatements = new PlayerStatement[MAXPLAYERSTATEMENTS];
    }

}

public class DialogueTree {

    private static HashMap<String, DialogueTree> dialogues = new HashMap<String, DialogueTree>();
    // changing: the above so that only finished dialogues go here, to be summoned for NPCs.
    // currently open/in-editing dialogues, which are not finished, will be saved to the FS
    // command's static hash map.

    String name;
    String ID; // this is also the filename it is saved under... which may be a problem later
                // so, make sure to delete previous file names when someone renames or transfers
                // ownership of their tree
    ArrayList<UUID> sharedWriters;
    String playerOwner; // owns the file/tree - used for filename
    boolean beingEdited = false;


    NPCStatement currentNode;
    NPCStatement rootNode;


    public DialogueTree(String givenTreeName, String playerName) {
        name = givenTreeName; // this is assuming valid name
        playerOwner = playerName;
        rootNode = new NPCStatement();
        currentNode = rootNode;

        beingEdited = true;
    }

    public static void addDialogueToExisting(DialogueTree newTree) {
        // check attributes exit
        if (newTree.name != null && newTree.playerOwner != null && newTree.rootNode != null) {
            // TD: check alphabetic tree name
            String fName = newTree.playerOwner + "_" + newTree.name;
            // this function doesn't check for duplicates
            DialogueTree.dialogues.put(fName, newTree);
            System.out.println("Added tree with treename: " + fName);

        } else {
            // TD: print error: can't add w/ null attributes
            System.out.println("Error: couldn't add new dialogue w null attributes");
            return;
        }


    }


    // this does the same thing as getDialogueForPlayer, but takes the full filename "playername_treename"
    public static DialogueTree getDialogueForFName(String givenFName) {
        // NOTE: will need two checks:
        // 1) from in-memory
        return dialogues.get(givenFName);

        // 2) load from file (after saving has been implemented
    }


    public static DialogueTree getDialogueForPlayer(String treeName, String playerName) {
        // assemble the file name / uniqueID for the tree
        String treeIDString = playerName + "_" + treeName;
        return getDialogueForFName(treeIDString);
    }

    public void addPMsgToCurrent(String givenPMsg, DError cmdError) {

        if (currentNode.numPStatements == NPCStatement.MAXPLAYERSTATEMENTS) {
            cmdError.setMsg("Can't add player message; have reached limit.");
            cmdError.type = DErrorType.INVALIDTREE;
            return;
        }


        PlayerStatement newPStatement = new PlayerStatement(givenPMsg, currentNode);
        currentNode.numPStatements++;
        currentNode.pStatements[currentNode.numPStatements - 1] = newPStatement;

    }

    //activePDialogue.addNPCMsgToPmsg((selectedPNum - 1), thisNPCMsg, cmdError);
    public void addNPCMsgToPMsg(int givenPIndex, String givenNPCMsg, DError error) {

        if (givenPIndex >= currentNode.numPStatements) {
            error.type = DErrorType.OUTOFBOUNDS;
            error.msg = "Please select a valid player statement to add this message to.";
            return;
        }


        NPCStatement newNPCStatement = new NPCStatement(givenNPCMsg);
        newNPCStatement.playerPrevious = currentNode.pStatements[givenPIndex];
        currentNode.pStatements[givenPIndex].npcNode = newNPCStatement;


/*
 NPCStatement newNPCStatement = new NPCStatement();
                    // TD: proper constructors, currently the dialogue class sets stuff when
                    // new dialogues are created so need a blank one and a msg one
                    newNPCStatement.msg = thisNPCMsg;
                    newNPCStatement.playerPrevious = activePDialogue.currentNode.pStatements[selectedPNum - 1];

                    activePDialogue.currentNode.pStatements[selectedPNum - 1].npcNode = newNPCStatement;
 */


    }

    // trying out returning a string msg for errors these next two functions...
    // TD: figure out whether to do this with all setter methods
    // or remove it here for consistency
    public String selPStatement(int PIndex) {
        if (currentNode.numPStatements == 0) return "No player statements to select!";
        if (currentNode.pStatements[PIndex] == null || currentNode.pStatements[PIndex].npcNode == null) return "Invalid selection.";

        currentNode = currentNode.pStatements[PIndex].npcNode;
        return "";
    }

    public void selBack(DError error) {
        if (currentNode == rootNode) {
            error.type = DErrorType.INVALIDTREE;
            error.msg = "Can't go up one level; at base node!";
            return;
        }
        currentNode = currentNode.playerPrevious.npcPrevious;

    }

    public String delPStatement(int givenPNum) {
        if (currentNode.numPStatements == 0) return "No player statements to delete!";
        if (currentNode.pStatements[givenPNum] == null) return "Invalid selection.";

        //see below pseudocode - must also delete everything attached to those nodes
        // to avoid memory leaks!
        // unless Java takes care of it automatically?
        //if (givenPNum)
        currentNode.pStatements[givenPNum] = null;
        currentNode.numPStatements -= 1;
        return "";
    }

    public String delNPCStatement(int givenPNum) {

        return "";
    }

    /*
    if (givenInt == 1 and currentNode.numPlayerStatements == 1) then
		currentNode.playerStatements = {}
	else
		local shiftNum = givenInt + 1
		--local shiftEnd = numPlayerStatements
		while (shiftNum <= currentNode.numPlayerStatements) do
			currentNode.playerStatements[shiftNum - 1].msg = currentNode.playerStatements[shiftNum].msg
			shiftNum = shiftNum + 1
		end
	end

     */


    /*
    if (givenInt == 1 and currentNode.numPlayerStatements == 1) then
		currentNode.playerStatements = {}
	else
		local shiftNum = givenInt + 1
		--local shiftEnd = numPlayerStatements
		while (shiftNum <= currentNode.numPlayerStatements) do
			currentNode.playerStatements[shiftNum - 1].msg = currentNode.playerStatements[shiftNum].msg
			shiftNum = shiftNum + 1
		end
	end
     */


    // utility/navigation, also for editing use
    public void printCurrentNode(Player player) {
        // print the current node's msg
        if (currentNode == null) {
            throw new NullPointerException("Error: tree's current node is null");
        }

        if (currentNode.msg == null || currentNode.msg == "") {
            player.sendMessage("NPC: ");
        } else {
            player.sendMessage("NPC: " + currentNode.msg);
        }

        // print all player responses - add * if pmsg has an npc msg tied to it
        String hasNPCMsg = "";
        if (currentNode.pStatements.length > 0) {
            for (int i = 0; i < currentNode.numPStatements; i++) {
                if (currentNode.pStatements[i].npcNode != null) {
                    hasNPCMsg = "* ";
                } else { hasNPCMsg = "";}

                player.sendMessage(hasNPCMsg + (i + 1) + ". " + currentNode.pStatements[i].msg);
            }
        }

    }

    /* --- rewriting below, as now NPCs will have their own instance of a tree.
    //********* functions for players speaking to NPCs.
    //********* these are a mess. Must fix them next
    // give player instance or return a string to print to the player?
    // this function: probably should consolidate with printCurrentNode()
    // maybe add a pass by reference endDialogue parameter?
        // conditions for ending: error, invalid, or no player responses available
    public void printNodeForNPCName(String npcName, Player player) {
        if (currentNode == null) {
            throw new NullPointerException("Error: tree's current node is null");
            // close the active tree for that player
        }

        if (currentNode.msg == null || currentNode.msg == "") {
            player.sendMessage(npcName + ": ");
        } else {
            player.sendMessage(npcName + ": " + currentNode.msg);
        }

        // print all player responses - add * if pmsg has an npc msg tied to it
        String hasNPCMsg = "";
        if (currentNode.pStatements.length > 0) {
            for (int i = 0; i < currentNode.numPStatements; i++) {
                if (currentNode.pStatements[i].npcNode != null) {
                    hasNPCMsg = "* ";
                } else { hasNPCMsg = "";}

                player.sendMessage(hasNPCMsg + (i + 1) + ". " + currentNode.pStatements[i].msg);
            }
        }
    }

    public String sayPlayerResponse(int givenResponse) {
        if (currentNode.numPStatements == 0) return "No player statements to select!";
        if (currentNode.pStatements[givenResponse] == null) return "Invalid selection.";

        currentNode = currentNode.pStatements[givenResponse].npcNode;
        return "";
    }
    */
}

