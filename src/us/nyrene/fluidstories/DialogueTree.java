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

    public PlayerStatement(String givenMsg, NPCStatement givenPrevNode) {
        if (givenMsg != null) {
            msg = givenMsg;
        } else {
            System.out.println("Can't add null msg to in PlayerStatement!");
            msg = "";
        }

        if (givenPrevNode != null) {
            npcPrevious = givenPrevNode;
        } else {
            System.out.println("Can't add null NPCStatement in PlayerStatement()");
        }
    }


    // copy constructor - this won't work, have to set the playerPrevious using the new object.
    // must use other copy function
    public PlayerStatement(PlayerStatement pStatementToCopy, NPCStatement newNPCPrevious) {
        msg = pStatementToCopy.msg;
        npcPrevious = newNPCPrevious;
    }

    public static PlayerStatement copyPStatement(PlayerStatement pStatementToCopy, NPCStatement newNPCPrevious) {
        PlayerStatement newPStatement = new PlayerStatement(pStatementToCopy, newNPCPrevious);
        if (pStatementToCopy.npcNode != null) {
            newPStatement.npcNode = new NPCStatement(pStatementToCopy.npcNode, pStatementToCopy);
        }

        return newPStatement;
    }
}

// other mentions of "node" refer to this.  should probably change names
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

    // copy constructor
    public NPCStatement(NPCStatement nodeToCopy, PlayerStatement newPlayerPrevious) {
        System.out.println("test!");
        if (nodeToCopy == null) {
            System.out.println("DEBUG: In NPCStatement copy constructor, node is null");
        }

        msg = nodeToCopy.msg;
        pStatements = new PlayerStatement[MAXPLAYERSTATEMENTS];
        numPStatements = nodeToCopy.numPStatements;

        for (int i = 0; i < numPStatements; i++) {
            // recursively copy player statements, which will in turn copy each node
            pStatements[i] = PlayerStatement.copyPStatement(nodeToCopy.pStatements[i], this);
        }

        if (newPlayerPrevious != null) {
            playerPrevious = newPlayerPrevious;
        }

    }

    public NPCStatement(String givenMsg) {
        msg = givenMsg;
        pStatements = new PlayerStatement[MAXPLAYERSTATEMENTS];
    }


    public void setMsg(String newMsg) {
        msg = newMsg;
    }

}

public class DialogueTree {

    private static HashMap<String, DialogueTree> dialogues = new HashMap<String, DialogueTree>();
    // changing: the above so that only finished dialogues go here, to be summoned for NPCs.
    // currently open/in-editing dialogues, which are not finished, will be saved to the FS
    // command's static hash map.
    // TD: remove above. DialogueManager has this

    private static HashMap<UUID, NPCStatement> playerBookmarks = new HashMap<UUID, NPCStatement>();
    // keeps track of where each player is in the dialogue

    String name;
    String ID; // this is also the filename it is saved under... which may be a problem later
                // so, make sure to delete previous file names when someone renames or transfers
                // ownership of their tree
    ArrayList<UUID> sharedWriters;
    UUID playerOwner; // owns the file/tree - used for filename


    NPCStatement currentNode; // this is for editing. probably need to change to editingNode or something similar
    NPCStatement rootNode;


    public DialogueTree(String givenTreeName, UUID playerID) {
        name = givenTreeName; // this is assuming valid name
        playerOwner = playerID;
        rootNode = new NPCStatement();
        currentNode = rootNode;
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

    public static DialogueTree copyTree(DialogueTree treeToCopy) {
        DialogueTree newTree = new DialogueTree(treeToCopy.name, treeToCopy.playerOwner);

        // Need to re-implement the copy node function for consistency w/ copy pmsg function.
        // or just leave it for now... will need to be revisited if those class definitions change.
        if (treeToCopy.rootNode == null) {
            System.out.println("DEBUG: attempting to copy root node; it is null");
        }

        if (treeToCopy.rootNode.msg == null || treeToCopy.rootNode.msg == "") {
            System.out.println("DEBUG: Attempting to copy empty message");
        }
        newTree.rootNode = new NPCStatement(treeToCopy.rootNode, null);
        newTree.currentNode = newTree.rootNode;
        System.out.println("DEBUG: In the copy tree function");
        return newTree;

    }

    // Might end up re-incorporating this, as a copy pmsg function had to be created because
    // the new pmsg object had to be assigned before the copy was over (hence, couldn't just use
    // copy constructor.
    /*
    public static NPCStatement copyNode(NPCStatement nodeToCopy) {
        NPCStatement newNode = new NPCStatement();
        // copy basic attributes
        newNode.setMsg(nodeToCopy.msg);
        newNode.numPStatements = nodeToCopy.numPStatements;

        // pStatements array is already set to max size.
        // if the MAXPSTATEMENT info doesn't match up, abort
        if (nodeToCopy.numPStatements > NPCStatement.MAXPLAYERSTATEMENTS) {
            return null;
        }

        for (int i = 0; i < nodeToCopy.numPStatements; i++) {
            // copy p statements, then for each one, if there is
            // an NPC node, call this function again
            newNode.pStatements[i] = new PlayerStatement(nodeToCopy.pStatements[i].msg, newNode);
            if (nodeToCopy.pStatements[i].npcNode != null) {
                newNode.pStatements[i].npcNode = copyNode(nodeToCopy.pStatements[i].npcNode);
            }
        }

        return newNode;
    }
    */



    public String addPMsgToCurrent(String givenPMsg) {

        if (currentNode.numPStatements == NPCStatement.MAXPLAYERSTATEMENTS) {
            return "You have reached the maximum number of player statements for this node.";
        }


        PlayerStatement newPStatement = new PlayerStatement(givenPMsg, currentNode);
        currentNode.numPStatements++;
        currentNode.pStatements[currentNode.numPStatements - 1] = newPStatement;

        return "";

    }

    //activePDialogue.addNPCMsgToPmsg((selectedPNum - 1), thisNPCMsg, cmdError);
    public String addNPCMsgToPMsg(int givenPIndex, String givenNPCMsg) {

        if (givenPIndex >= currentNode.numPStatements) {
            return "Please select a valid number.";
        }


        NPCStatement newNPCStatement = new NPCStatement(givenNPCMsg);
        newNPCStatement.playerPrevious = currentNode.pStatements[givenPIndex];
        currentNode.pStatements[givenPIndex].npcNode = newNPCStatement;

        return "";
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

    public String delPStatement(int givenPIndex) {
        if (currentNode.numPStatements == 0) return "No player statements to delete!";
        if (currentNode.pStatements[givenPIndex] == null) return "Invalid selection.";

        //see below pseudocode
        //if (givenPNum)
        currentNode.pStatements[givenPIndex] = null;
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
    // TD: change to return string that contains current node info,
    // rather than passing the player object in
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

    // TD: this function needs major refactoring
    public String playerSelectedDialogueOption(int selection, UUID playerID) {
        // get the node for this player, and if it's a valid option, advance it
        // return the text from here, because if the next NPC node is null, the dialogue
        // needs to end.

        NPCStatement fetchedNode = playerBookmarks.get(playerID);
        if (fetchedNode == null) {
            return "";
        }
        if (selection < 1 || selection > fetchedNode.numPStatements) {
            return "Invalid selection";
        }

        String returnStr;
        selection --; // use as index now

        // two primary cases: either there's an NPC node for that option, or there isn't one.
        // if there isn't, the conversation ends, and the player sees an empty string.

        if (fetchedNode.pStatements[selection].npcNode == null) {
            returnStr =  "You: " + fetchedNode.pStatements[selection].msg;
            playerEndedConversation(playerID);
        } else {
            // if there is a node, and that NPC node has no player options, then the conversation also ends,
            // and the player sees the NPC message. .....
            NPCStatement nextNode = fetchedNode.pStatements[selection].npcNode;

            if (nextNode.pStatements.length == 0) {
                returnStr = nextNode.msg;
                playerEndedConversation(playerID);
            } else {
                // .... otherwise, set the player's current node to the selection, and return options for that.
                playerBookmarks.put(playerID, nextNode);
                returnStr = "You: " + fetchedNode.pStatements[selection].msg;
                returnStr += "\n" + getNodeTalkingTextForPlayer(playerID);

            }

        }

        return returnStr;

    }


    public String playerStartedConversation(UUID newPlayerID) {
        // create a new bookmark for this player, and return the convo string.
        playerBookmarks.put(newPlayerID, rootNode);

        return getNodeTalkingTextForPlayer(newPlayerID);
    }

    public String playerEndedConversation(UUID thisPlayerID) {
        playerBookmarks.remove(thisPlayerID);

        return "";
    }

    public boolean hasActivePlayer(UUID playerID) {
        if (playerBookmarks.get(playerID) != null) {
            return true;
        }

        return false;
    }

    public String getNodeTalkingTextForPlayer(UUID thisPlayerID) {
        // similar to printCurrentNode for editing, but if we're on a null or empty node,
        // return an empty string.
        // also, no signifier for if an option has an NPC node attached to it.

        NPCStatement curPlayerNode = playerBookmarks.get(thisPlayerID);
        if (curPlayerNode == null) {
            return "\n You are not currently in a conversation with anyone.";
        }

        String nodeString;

        if (curPlayerNode.msg == null || curPlayerNode.msg == "") {
            nodeString = "NPC: ";
        } else {
            nodeString = "NPC: " + curPlayerNode.msg;
        }

        // print all player responses - add * if pmsg has an npc msg tied to it
        if (curPlayerNode.pStatements.length > 0) {
            for (int i = 0; i < curPlayerNode.numPStatements; i++) {
                nodeString += "\n" + ((i + 1) + ". " + curPlayerNode.pStatements[i].msg);
            }
        }

        return nodeString;
    }


}

