package us.nyrene.fluidstories;
import java.util.UUID;
import java.util.HashMap;

public class DialogueManager {
    // trees actively being edited, can't be assigned to NPCs
    private HashMap<String, DialogueTree> editingDialogues = new HashMap<String, DialogueTree>();

    // trees that are no longer being edited/are finished. Can be assigned to NPCs
    private HashMap<String, DialogueTree> closedDialogues = new HashMap<String, DialogueTree>();


    public String saveDialogue(DialogueTree givenDialogue) {
        //

        // later: write to file

        return "";
    }

    public String closeDialogue(UUID playerID) {
        // remove the dialogue from active editing.
        // don't bother prompting to save for now - just remove it, user can
        // decide whether or not to save

        return "";
    }

    public String createNewDialogue(String newName, UUID playerID) {
        // file format will be 1 file per player. Top: list of player's trees
        // then a JSON item for each dialogue

        // check for duplicate name
        // format of file name will be UUID


        return "";
    }

}
