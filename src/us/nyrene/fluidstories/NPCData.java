package us.nyrene.fluidstories;

import org.bukkit.entity.EntityType;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Entity;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.Map;
import org.bukkit.Location;
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

    String name;
    String description;
    UUID entityID;
    transient Location location;
    // DialogueTree dialogue;
    String dialogueTreeID = "";
    UUID playerOwner;

    public NPCData(String givenName, UUID givenEntityID, Location givenLocation) {
        entityID = givenEntityID;
        name = givenName;
        location = givenLocation;
        description = "";

    }

    public String getNPCDescription() {
        if (description == null) {
            return "";
        }

        return description;
    }

    public void setNPCDescription(String givenDescription) {
        if (givenDescription == null || givenDescription == "") {
            //log.info("Error: given NPC description was null");
            return;
        }

        description = givenDescription;
    }

    public Location getLocation() {
        return location;
    }

    public void setDialogue(String newDialogueID) {
        if (newDialogueID != null) {
            dialogueTreeID = newDialogueID;
        }
        // TD: error if null
    }

    public String getDialogueTreeID() {
        return dialogueTreeID;
    }


}
