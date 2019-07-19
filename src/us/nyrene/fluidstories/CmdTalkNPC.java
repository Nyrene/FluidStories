package us.nyrene.fluidstories;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Entity;

import java.util.HashMap;

/*
Potential ideal design for this class:

this class, being the interface to the player, should print all strings to the player directly.
will use error and end params to determine when/if dialogues have ended.

the dialogue strings will be generated by the dialogue class.

 */


public class CmdTalkNPC implements CommandExecutor {
    // dialogues active by NPC name/string
    // list of players and which node/dialogue they are currently on
    private static HashMap<String, DialogueTree> activeDialogues = new HashMap<String, DialogueTree>();

    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {
        if (pSender instanceof Player) {

            // check that the args are correct: one argument, the name of the NPC
            // This command should open the dialogue tree for that player (add a placemarker for them)
            // and then the /say command will be used to select dialogue options.
            if (args.length < 1) {
                return false;
            }

            Player writer = (Player) pSender;
            main.getInstance().getNPCMgr().startConversationWithNPCForPlayer(args[0], writer.getUniqueId());

        }

        return true;
    }
}
