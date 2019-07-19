package us.nyrene.fluidstories;
import java.util.HashMap;
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


    public NPCManager() {
        activeNPCs = new HashMap<String, NPCData>();
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
            Villager thisVillager = ((Villager) iterator.next());
            if (thisVillager.getUniqueId() == fetchedNPC.entityID) {
                thisVillager.remove(); // TD see if this actually immediately removes the villager?
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

    public void assignDialogueToNPCForPlayer(String npcName, String dialogueTreeName, UUID playerID, DError error) {
        NPCData fetchedNPC = getNPCDataWithNameForPlayer(npcName, playerID, error);

        if (fetchedNPC == null) {
            return; // TD replace with error
        }

        //if (newDialogue != null) {
            // fetchedNPC.assignDialogue(dialogue);
        //}
    }

    public void startConversationWithNPCForPlayer(String npcName, UUID playerID) {

    }

}
