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
import java.util.UUID;



public class CmdSpawnNPC implements CommandExecutor {

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
            Location blockLocation = block.getLocation();

            // spawn an NPC there -- for custom NPCs, may need to use:
            //T	spawn​(Location location, Class<T> clazz) (World class)

            // for now, using generic one
            World thisWorld = blockLocation.getWorld();
            blockLocation.add(0, .5, 0);
            Entity spawnResult = thisWorld.spawnEntity(blockLocation,EntityType.VILLAGER);

            Villager newVillager = (Villager) spawnResult;

            // set its speed to 0, permanent = true, set name and make name visible
            newVillager.setRemoveWhenFarAway(false);
            newVillager.setCustomName(args[0]);
            newVillager.setCustomNameVisible(true);
            newVillager.setAI(false);
            UUID newVID = newVillager.getUniqueId();
            // create a new NPCData item to associate with this NPC
            NPCData newNPC = new NPCData(args[0], newVID);
            NPCData.preserveNPCData(newNPC, args[0]);


            return true;
        }

        return false;
    }
}
