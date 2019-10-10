package us.nyrene.fluidstories;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Collection;
import java.util.Iterator;


public class NPCManager {
    // NPCs are ID'd by name, which is why they must
    // all be kept unique. Change later on?
    private HashMap<String, NPCData> activeNPCs;
    private FileWriter fwriter;


    public NPCManager() {
        activeNPCs = new HashMap<String, NPCData>();
        try {fwriter = new FileWriter("npcs");}
        catch (IOException e) {
            System.out.println("Error opening npcs file: " + e);
        }
    }

    public void spawnNPC(String newNPCName, Location targetLocation, UUID playerOwnerUUID) {
        // check name conflict, error if npc already exists
        if (newNPCName == null || targetLocation == null || playerOwnerUUID == null) {
            // TD: error
            return;
        }

        if (activeNPCs.get(newNPCName) != null) {
            // TD: error message w/ conflict
            return;
        }

        // spawn at given location
        World thisWorld = targetLocation.getWorld();
        targetLocation.add(0, .5, 0);
        Entity spawnResult = thisWorld.spawnEntity(targetLocation,EntityType.VILLAGER);

        Villager newVillager = (Villager) spawnResult;
        newVillager.setRemoveWhenFarAway(false);
        newVillager.setCustomName(newNPCName);
        newVillager.setCustomNameVisible(true);
        newVillager.setAI(false);
        UUID newVID = newVillager.getUniqueId();


        NPCData newNPC = new NPCData(newNPCName, newVID, targetLocation);
        activeNPCs.put(newNPCName, newNPC);



        // save info to player file using the storagemanager
            // ---placeholder
    }

    public String removeNPC(String npcName, UUID playerID) {
        DError errInst = new DError();
        NPCData fetchedNPC = getNPCDataWithNameForPlayer(npcName, playerID, errInst);
        if (errInst.type != DErrorType.NOERROR) {
            return errInst.msg;
        }

        if (fetchedNPC == null) {
            return "Unspecified error.";
        }

        World thisWorld = fetchedNPC.location.getWorld();
        Collection<Entity> foundEntities =  thisWorld.getNearbyEntities(fetchedNPC.getLocation(), 2.0, 2.0, 2.0);
        Iterator<Entity> iterator = foundEntities.iterator();

        // while loop
        while (iterator.hasNext()) {
            Entity thisEntity = iterator.next();
            if (thisEntity.getUniqueId() == fetchedNPC.entityID) {
                thisEntity.remove();
                activeNPCs.remove(npcName);
            }
        }

        return "";
    }

    public String getNPCDesc(String npcName) {
        // don't bother checking ownership here - just reading one attr
        NPCData fetchedNPC = activeNPCs.get(npcName);
        if (fetchedNPC == null) {
            // TD: error
            return "No NPC with that name exists!";
        }

        if (fetchedNPC.getNPCDescription() != "") {
            return fetchedNPC.getNPCDescription();
        } else {
            return "You can't determine anything about this person.";
        }

        //return "";
    }

    public NPCData getNPCDataWithNameForPlayer(String npcName, UUID playerID, DError error) {
        // get the named NPC and check that the requesting player is an owner
        NPCData fetchedNPC = activeNPCs.get(npcName);

        if (fetchedNPC == null) {
            error.setMsg("No NPC with that name was found.");
            error.setType(DErrorType.NULLNODE);
            return null;
        }

        if (fetchedNPC.playerOwner != playerID) {
            error.setMsg("You do not have permissions to modify this NPC.");
        }

        return fetchedNPC;
    }

    public void setNPCDescriptionForPlayer(String npcName, String newDesc, UUID playerID, DError error) {
        NPCData fetchedNPC = getNPCDataWithNameForPlayer(npcName, playerID, error);

        if (fetchedNPC == null || error.type != DErrorType.NOERROR) {
            return;
        }

        fetchedNPC.setNPCDescription(newDesc);
    }

    public String assignDialogueToNPCForPlayer(String npcName, String dialogueTreeName, UUID playerID) {
        DError error = new DError();
        NPCData fetchedNPC = getNPCDataWithNameForPlayer(npcName, playerID, error);

        if (fetchedNPC == null || error.type != DErrorType.NOERROR) {
            return error.msg;
        } else {
            String dialogueFullName = playerID + "_" + dialogueTreeName;
            if (main.getInstance().getDialogueManager().dialogueWithNameExists(dialogueFullName) == true) {
                fetchedNPC.setDialogue(dialogueFullName);
            }

        }

        return "Dialogue assigned to NPC.";
    }

    public boolean isNPCWithNameInRangeOfLocation(String NPCName, Location location) {

        return true;
    }

    public String getDialogueIDForNPCWithName(String npcName) {
        if (activeNPCs.get(npcName) == null) {
            return "";
        }

        NPCData fetchedNPC = activeNPCs.get(npcName);
        return fetchedNPC.getDialogueTreeID();
    }

    // test code within branch 'persistence'
    public void writeNPCData() {
        Gson gsonObj = new GsonBuilder().setPrettyPrinting().create();
        Iterator it = activeNPCs.entrySet().iterator();
        String jsonResult;
        // below copied from: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            NPCData thisData = (NPCData) pair.getValue();
            jsonResult = gsonObj.toJson(thisData);
            try { fwriter.write(jsonResult + "\n"); }
            catch (IOException e) {
                System.out.println("Error: could not write npcdata to file, " + e);
            }
            System.out.println("Successfully saved NPC data.");
        }

        try {fwriter.close(); }
        catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }



}
