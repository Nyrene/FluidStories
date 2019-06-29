package us.nyrene.fluidstories;

import org.bukkit.entity.EntityType;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Entity;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

/*
    Subclass for NPC Villager entities/instances
    This will ot
        will need to have:
        - ID
        - name
        - associated dialogue tree (s)
        - X location probably isn't necessary - can look this info up
        - player owners



        things to set upon creation:
        - ID (this probably doesn't need to be set? rather kept track of
        - name (view name)
        - Player owner (who created it)
        - Dialogue tree (initially, unset)

 */


public class NPCData {

    private static Map<String, NPCData> createdEntities = new HashMap<String, NPCData>(); //name, NPC Data - NPCs will need to have unique names

    String name;
    String description;
    UUID entityID;
    Map<String, Integer> playerOwners; // map with usernames, ID's

    public NPCData(String givenName, UUID givenEntityID) {
        entityID = givenEntityID;
        name = givenName;
        description = "";

    }

    public static void preserveNPCData(NPCData givenNPC, String givenNPCName) {
        createdEntities.put(givenNPCName, givenNPC);
        // null pointer exception here, may have to add the info to the
    }

    public static NPCData getNPCWithName(String givenName) {

        return createdEntities.get(givenName);
    }

    public UUID getAssociatedEntityID() {
        return entityID;
    }

    public String getNPCDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    public void setNPCDescription(String givenDescription) {
        if (givenDescription == null) {
            //log.info("Error: given NPC description was null");
            return;
        }

        description = givenDescription;
    }



}
