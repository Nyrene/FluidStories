package us.nyrene.fluidstories;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;


public class CmdSpawnNPC implements CommandExecutor {

    private NPCManager npcMgr;

    // public CmdSpawnNPC(NPCManager )

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {

        if (pSender instanceof Player) {
            //writer.sendMessage("This is the command!");

            // check that the args are correct: one argument, the name of the NPC
                if (args.length != 1) {
                    return false;
                }

            Player writer = (Player) pSender;
            // get the player's look location
            Block block = writer.getTargetBlock(null, 100);

            // TD: error handling or wrap in try
            main.getInstance().getNPCMgr().spawnNPC(args[0], block.getLocation(), writer.getUniqueId());

            return true;
        }

        return false;
    }
}
