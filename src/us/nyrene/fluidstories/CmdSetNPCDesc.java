package us.nyrene.fluidstories;

//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
//import org.bukkit.entity.Villager;
import org.bukkit.command.CommandExecutor;

public class CmdSetNPCDesc implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {

        if (pSender instanceof Player) {
            //writer.sendMessage("This is the command!");

            // check that the args are correct: 2+ arguments, the name of the NPC and the description
            if (args.length == 0 || args.length < 2) {
                return false;
            }

            Player writer = (Player) pSender;

            if (NPCData.getNPCWithName(args[0]) != null) {
                NPCData npc = NPCData.getNPCWithName(args[0]);
                // get the args string
                String descString = "";
                for (int i = 1; i < args.length; i++) {
                    descString = descString + " " + args[i];
                }

                // set that to the description
                npc.description = descString;
            } else {
                writer.sendMessage("No one with that name is nearby.");
            }

            return true;
        }

        return false;
    }
}