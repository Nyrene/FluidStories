package us.nyrene.fluidstories;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CmdSay implements CommandExecutor {
    // dialogues active by NPC name/string
    // list of players and which node/dialogue they are currently on


    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {
        if (pSender instanceof Player) {

            // check that the args are correct: one argument, the name of the NPC
            // This command should open the dialogue tree for that player (add a placemarker for them)
            // and then the /say command will be used to select dialogue options.
            if (args.length != 1) {
                pSender.sendMessage("Usage: /say <option>");
                return false;
            }

            Player player = (Player) pSender;
            int sel = Integer.valueOf(args[1]);

            String result = main.getInstance().getDialogueManager().playerSelected(sel, player.getUniqueId());
            player.sendMessage(result);
        }

        return true;
    }
}