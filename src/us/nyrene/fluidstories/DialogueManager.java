package us.nyrene.fluidstories;
import java.util.UUID;
import java.util.HashMap;

public class DialogueManager {
    private HashMap<String, DialogueTree> editingDialogues = new HashMap<String, DialogueTree>();


    public String saveDialogue(DialogueTree givenDialogue) {


        // later: write to file

        return "";
    }

    public String closeDialogue(UUID playerID) {

    }

    public String createNewDialogue(String newName, UUID playerID) {
        // file format will be 1 file per player. Top: list of player's trees
        // then a JSON item for each dialogue

        // check for duplicate name
        // format of file name will be UUID


        return "";
    }

}
