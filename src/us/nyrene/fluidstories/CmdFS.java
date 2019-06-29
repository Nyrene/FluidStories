package us.nyrene.fluidstories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class CmdFS implements CommandExecutor {
    private static HashMap<String, DialogueTree> editingDialogues = new HashMap<String, DialogueTree>();
    DError cmdError = new DError(DErrorType.NOERROR, "");
    // ^ keyed by: player, dialogue tree. If another player tries to open the dialogue for editing,
    // block them

    // need to update this to function similar to the plan for CmdTalkNPC:
    // use return strings to print to the player, and an error param.

    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {

        if (pSender instanceof Player) {
            Player writer = (Player) pSender;

            if (args.length == 0) {
                return false;
            }

            String subcommand = args[0];
            DialogueTree activePDialogue = editingDialogues.get(writer.getName());
            String returnString = "";

            switch (subcommand) {
                case "createdialogue":
                    if (args.length != 2) { return false; }
                    String givenTreeName = args[1];
                    if (!checkAlphabetic(givenTreeName)) {
                        writer.sendMessage("Please use only alphabetic characters in your dialogue name.");
                        return false;
                    }

                    if (givenTreeName.length() > 15) {
                        writer.sendMessage("Please give a dialogue name less than 15 characters");
                        return false;
                    }

                    // see if that name already exists: naming scheme for dialogue
                    // how to get the name: playername + argsname
                    // later: allow for 3 params to load other DTrees, one with a diff player's name + name

                    // TD: check that another player doesn't currently have it open
                    //      (how? use an editing attribute?)

                    // TD: check that the player doesn't already have something else open.

                    if (editingDialogues.get(writer.getName()) != null) {
                        writer.sendMessage("You are already editing a dialogue. Please close it before proceeding.");
                        return false;
                    }

                    // passed validity checks -- create a new dialogue with given name
                    DialogueTree newTree = new DialogueTree(givenTreeName, writer.getName());

                    // use the dialogue setter function to add it
                    // don't add it to the master DialogueTree list.
                    // add it to those being currently edited - saving + validating tree will add it to master
                    // list.
                    editingDialogues.put(writer.getName(), newTree);

                    writer.sendMessage("New tree " + newTree.name + " created successfully!");
                    break;


                case "setnpcmsg":
                    // get the current node for the current player and dialogue tree
                    // at this point, tree should be in memory.
                    // TD: tree function for this
                    if (activePDialogue == null) {
                        writer.sendMessage("No active tree! Please create or load one.");
                        return true;
                    }
                    // get the args string
                    // TD: refactor into function; need to do this in another spot too
                    String descString = "";
                    for (int i = 1; i < args.length; i++) {
                        descString = descString + " " + args[i];
                    }

                    activePDialogue.currentNode.msg = descString;
                    activePDialogue.printCurrentNode(writer);

                    break;

                case "addpmsg":
                    if (activePDialogue == null) {
                        writer.sendMessage("No active tree! Please create or load one.");
                        return true;
                    }

                    String pMsgString = "";
                    for (int i = 1; i < args.length; i++) {
                        pMsgString = pMsgString + " " + args[i];
                    }

                    // attempt to add player statement.
                    // TD: must move stuff like this to accessor/setter methods
                    PlayerStatement newPStatement = new PlayerStatement(pMsgString, activePDialogue.currentNode);
                    activePDialogue.addPMsgToCurrent(newPStatement);
                    activePDialogue.printCurrentNode(writer);
                    break;

                case "addnpcmsg":
                    if (activePDialogue == null) {
                        writer.sendMessage("No active tree! Please create or load one.");
                        return true;
                    }

                    // get number param, which is args[1], and convert to number.
                    // check that it is a valid num which has a pmsg associated
                    // if that passes, grab the rest of the message (loop starting at i)

                    Integer thisPNum = Integer.valueOf(args[1]);
                    if (activePDialogue.currentNode.pStatements[thisPNum - 1] == null) {
                        writer.sendMessage("Please provide a valid number.");
                        return true;
                    }

                    String thisNPCMsg = "";
                    for (int i = 2; i < args.length; i++) {
                        thisNPCMsg = thisNPCMsg + " " + args[i];
                    }

                    NPCStatement newNPCStatement = new NPCStatement();
                    // TD: proper constructors, currently the dialogue class sets stuff when
                    // new dialogues are created so need a blank one and a msg one
                    newNPCStatement.msg = thisNPCMsg;
                    newNPCStatement.playerPrevious = activePDialogue.currentNode.pStatements[thisPNum - 1];

                    activePDialogue.currentNode.pStatements[thisPNum - 1].npcNode = newNPCStatement;
                    activePDialogue.printCurrentNode(writer);
                    break;

                case "select":
                    if (activePDialogue == null) {
                        writer.sendMessage("No active tree! Please create or load one.");
                        return true;
                    }

                    if (args.length != 2) {
                        writer.sendMessage("usage: /fs select <#>");
                    }

                    // go to next node if it exists, use the class for it
                    // TD: update this method to follow returnstring/error/etc
                    returnString =  activePDialogue.selPStatement(Integer.valueOf(args[1]));
                    if (returnString != "") writer.sendMessage(returnString);
                    activePDialogue.printCurrentNode(writer);
                    break;

                case "back":
                    if (activePDialogue == null) {
                        writer.sendMessage("No active tree! Please create or load one.");
                        return true;
                    }

                    if (args.length != 1) {
                        writer.sendMessage("usage: /fs back");
                    }

                    returnString = activePDialogue.selBack();


                    if (returnString != "") writer.sendMessage(returnString);
                    activePDialogue.printCurrentNode(writer);

                    break;

                case "help":
                    writer.sendMessage("<list of available commands");
                    break;


                default:
                    writer.sendMessage("FS: Unrecognized subcommand.");
                    break;
            }

            return true;
        }

        return false;
    }

    // copied from https://codereview.stackexchange.com/questions/150597/checking-to-see-if-a-string-is-alphabetic
    public static boolean checkAlphabetic(String input) {
        for (int i = 0; i != input.length(); ++i) {
            if (!Character.isLetter(input.charAt(i))) {
                return false;
            }
        }

        return true;
    }


}
