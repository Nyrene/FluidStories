package us.nyrene.fluidstories;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdRemoveNPC implements CommandExecutor {

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


            // TD: error handling or wrap in try
            main.getInstance().getNPCMgr().removeNPC(args[0], writer.getUniqueId());

            return true;
        }

        return false;
    }
}
