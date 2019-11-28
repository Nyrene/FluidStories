package us.nyrene.fluidstories;

import org.bukkit.entity.EntityType;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Entity;
import java.util.logging.Logger;
import java.util.UUID;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Server;
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
    // it's a bunch of extra work to write custom serializationfor the location object,
    // while leaving default behavior for everything else.
    // because I am lazy, just keep track of xyz/world for now.
    transient Location location;
    private Double xCoord;
    private Double yCoord;
    private Double zCoord;
    private String worldName;
    // DialogueTree dialogue;
    private String dialogueTreeID = "";
    private UUID playerOwner;

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

    public Location getLocation() {
        // return location constructed from attributes

        return new Location(Bukkit.getWorld(worldName), xCoord, yCoord, zCoord);
    }

    public void setLocation(Location newLocation) {
        worldName = newLocation.getWorld().getName();
        xCoord = newLocation.getX();
        yCoord = newLocation.getY();
        zCoord = newLocation.getZ();

    }

    public void setNPCDescription(String givenDescription) {
        if (givenDescription == null || givenDescription == "") {
            //log.info("Error: given NPC description was null");
            return;
        }

        description = givenDescription;
    }


    public void setDialogue(String newDialogueID) {
        if (newDialogueID != null) {
            dialogueTreeID = newDialogueID;
        }
        // TD: error if null
    }

    public UUID getPlayerOwner() {
        return playerOwner;
    }

    public String getDialogueTreeID() {
        return dialogueTreeID;
    }


}
