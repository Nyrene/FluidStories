package us.nyrene.fluidstories;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;

import java.util.UUID;
import java.util.HashMap;
import java.util.List;

class WriterStats {
    UUID playerID;
    short numDialoguesCreated = 0;
    // map <"NPCName", "DialogueName">


}

public class DialogueManager {

    // trees actively being edited, can't be assigned to NPCs
    private HashMap<UUID, DialogueTree> editingDialogues = new HashMap<UUID, DialogueTree>();

    // trees that are no longer being edited/are finished. Can be assigned to NPCs
    private HashMap<String, DialogueTree> closedDialogues = new HashMap<String, DialogueTree>();

    short maxDialoguesPerPlayer = 8; // have a property for this later


    public String saveActiveDialogueForPlayer(UUID playerID) {
        // replace the talking dialogue hashmap entry with the current editing dialogue
        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);
        if (fetchedDialogue == null) {
            return "No active dialogue to save!";
        }

        String fullDName = playerID.toString() + "_" + fetchedDialogue.name;
        closedDialogues.put(fullDName, fetchedDialogue);

        // TD: update all NPCs that have this dialogue once this function is implemented
        // main.getInstance().getNPCMgr().updateNPCsWithUniqueDialogueName(fullDName);

        // later: write to file

        return "";
    }

    public String closeDialogue(UUID playerID) {
        // remove the dialogue from active editing.
        // don't bother prompting to save for now - just remove it, user can
        // decide whether or not to save

        if (getEditingDialogueForPlayer(playerID) != null) {
            editingDialogues.remove(playerID);
        }

        return "";
    }

    public boolean playerIsEditingDialogue(UUID playerID) {
        if (editingDialogues.get(playerID) != null ) {
            return true;
        }

        return false;
    }

    public DialogueTree openDialogueForPlayer(String dialogueName, UUID playerID) {
        // fetch the named tree for that player if possible, add it to editing dialogues

        // if it exists, deep copy the dialogue to the editingDialogues list (don't inadvertently
        // copy a reference and edit the talking version!)

        return null;
    }

    public DialogueTree getEditingDialogueForPlayer(UUID playerID) {
        // get this from the list of editing dialogues - this is for operating on active dialogues


        return null;
    }

    public DialogueTree openSavedDialogueForPlayer(String dialogueName, UUID playerID) {
        // later on, if it's not in the actively loaded saved dialogues (hashmap),
        // then check saved files and load from there
        // for now just assume it's in the active list of hashmaps
        String fullDialogueName = playerID.toString() + "_" + dialogueName;
        if (closedDialogues.get(fullDialogueName) == null) {
            return null;
        } else {
            return closedDialogues.get(fullDialogueName);
        }
    }



    public String createNewDialogue(String newName, UUID playerID) {
        // file format will be 1 file per player. Top: list of player's trees
        // then a JSON item for each dialogue

        // check for duplicate name
        // format of file name will be UUID


        // see if this player has a dialogue open already
        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);

        if (fetchedDialogue != null) {
            return "Please close any current dialogues before creating or opening a new one.";
        }

        String fullDialogueName = playerID.toString() + "_" + newName;
        if (closedDialogues.get(fullDialogueName) != null) {
            return "You already have a dialogue with that name. Please choose a different name.";
        }

        DialogueTree newDialogue = new DialogueTree(newName, playerID);
        editingDialogues.put(playerID, newDialogue);

        return "New tree " + newName + " created!";
    }


    public String deleteSavedDialogue(String dName, UUID playerID) {
        // remember to delete all instances of this dialogue from NPCs

        return "";
    }


    public String copyDialogueWithNameForPlayer(String existingDialogueName, String newDialogueName, UUID playerID) {
        // check that player has a dialogue with given name


        return "";
    }


    public String setNPCMsgInActiveDialogue(String newNPCMsg, UUID playerID) {
        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);
        if (fetchedDialogue == null) {
            return "No active dialogue to save!";
        }

        if (newNPCMsg == null || newNPCMsg == "") {
            return "Can't set empty message!";
        }

        fetchedDialogue.currentNode.setMsg(newNPCMsg);

        return "";
    }

}
