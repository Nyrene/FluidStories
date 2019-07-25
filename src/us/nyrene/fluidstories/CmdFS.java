package us.nyrene.fluidstories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class CmdFS implements CommandExecutor {
    DError cmdError = new DError(DErrorType.NOERROR, "");
    // ^ keyed by: player, dialogue tree. If another player tries to open the dialogue for editing,
    // block them

    // need to update this to function similar to the plan for CmdTalkNPC:
    // use return strings to print to the player, and an error param.

    // TD: there's a get instance of something in almost every switch case... refactor?

    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {
        cmdError.reset();
        if (pSender instanceof Player) {
            Player writer = (Player) pSender;

            if (args.length == 0) {
                return false;
            }

            String subcommand = args[0];
            DialogueTree activePDialogue = main.getInstance().getDialogueManager().getEditingDialogueForPlayer(writer.getUniqueId());
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

                    returnString = main.getInstance().getDialogueManager().createNewDialogue(givenTreeName, writer.getUniqueId());
                    writer.sendMessage(returnString);

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

                    String errString = main.getInstance().getDialogueManager().setNPCMsgInActiveDialogue(descString, writer.getUniqueId());
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

                    activePDialogue.addPMsgToCurrent(pMsgString, cmdError);
                    if (cmdError.type != DErrorType.NOERROR) writer.sendMessage(cmdError.msg);

                    activePDialogue.printCurrentNode(writer);
                    break;

                case "addnpcmsg":
                    if (activePDialogue == null) {
                        writer.sendMessage("No active tree! Please create or load one.");
                        return true;
                    }

                    // parse command
                    Integer selectedPNum = Integer.valueOf(args[1]);
                    String thisNPCMsg = "";
                    for (int i = 2; i < args.length; i++) {
                        thisNPCMsg = thisNPCMsg + " " + args[i];
                    }
                    int PIndex = selectedPNum - 1;
                    if (PIndex <= 0 || PIndex > NPCStatement.MAXPLAYERSTATEMENTS) {
                        writer.sendMessage("Please choose a number within the available range.");
                        return true;
                    }

                    activePDialogue.addNPCMsgToPMsg((PIndex), thisNPCMsg, cmdError);
                    if (cmdError.type != DErrorType.NOERROR) {
                        writer.sendMessage(cmdError.msg);
                    }
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

                    int selectedNum = Integer.valueOf(args[1]);
                    if (selectedNum < 1 || selectedNum > NPCStatement.MAXPLAYERSTATEMENTS) {
                        writer.sendMessage("Please use a number in a valid range.");
                        return true;
                    }

                    // go to next node if it exists, use the class for it
                    // TD: update this method to follow returnstring/error/etc
                    returnString =  activePDialogue.selPStatement(selectedNum - 1);
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

                    activePDialogue.selBack(cmdError);


                    if (cmdError.type != DErrorType.NOERROR) writer.sendMessage(cmdError.msg);
                    activePDialogue.printCurrentNode(writer);
                    break;

                case "save":
                    //
                    break;

                case "close":
                    // add it to list of active/available dialogues for player to assign
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
