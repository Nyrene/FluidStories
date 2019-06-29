package us.nyrene.fluidstories;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class CmdLookNPC implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {

        if (pSender instanceof Player) {
            //writer.sendMessage("This is the command!");
            Player writer = (Player) pSender;

            // check that the args are correct: 1 argument, the name of the selected NPC
            if (args.length != 1) {
                writer.sendMessage("Incorrect args");
                return false;
            }

            // TD: check to see if given NPC is in radius of player
            // maybe later on, have the player right click the entity instead

            // for now, just see if the NPC exists at all
            if (NPCData.getNPCWithName(args[0]) != null) {
                NPCData villager = NPCData.getNPCWithName(args[0]);
                if (villager.getNPCDescription().equals("")) {
                    writer.sendMessage("You can't determine anything about this villager.");
                    return true;
                }

                writer.sendMessage(villager.getNPCDescription());

            } else {
                writer.sendMessage("No NPC with that name exists!");
                return true;
            }



            return true;
        }

        return false;
    }

}
