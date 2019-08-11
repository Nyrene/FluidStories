package us.nyrene.fluidstories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class CmdSetNPCDialogue implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {

        if (pSender instanceof Player) {
            //writer.sendMessage("This is the command!");
            Player writer = (Player) pSender;

            // check that the args are correct: 1 argument, the name of the selected NPC
            if (args.length < 2) {
                writer.sendMessage("Usage: /SetNPCDialogue <NPC> <Dialogue name>");
                return false;
            }

            // TD: check to see if given NPC is in radius of player
            String returnStr = main.getInstance().getNPCMgr().assignDialogueToNPCForPlayer(args[0], args[1], writer.getUniqueId());
            writer.sendMessage(returnStr);

            return true;
        }

        return false;
    }

}
