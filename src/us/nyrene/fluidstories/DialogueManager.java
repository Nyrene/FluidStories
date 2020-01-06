package us.nyrene.fluidstories;
import com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;

import java.util.Iterator;
import java.util.UUID;
import java.util.HashMap;
import com.google.gson.Gson;
import org.json.simple.parser.JSONParser;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;

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

    // is having this map a bad practice? it's probably faster than doing multiple fetches
    // each time the player executes a dialogue command?
    // it feels wrong to repeatedly pull instances of dialogues just for read-only actions. Perhaps
    // callbacks make more sense. Or I'm overthinking a small side project.
    private HashMap<UUID, DialogueTree> speakingDialogues = new HashMap<UUID, DialogueTree>();

    short maxDialoguesPerPlayer = 8; // have a property for this later


    // for closed dialogues... should rename to make more explicit?
    public boolean dialogueWithNameExists(String dialogueName) {
        if (closedDialogues.get(dialogueName) != null) {
            return true;
        }

        return false;
    }

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
        return (editingDialogues.get(playerID));
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

        return "New tree '" + newName + "' created!";
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
            return "No active dialogue!";
        }

        if (newNPCMsg == null || newNPCMsg == "") {
            return "Can't set empty message!";
        }

        fetchedDialogue.currentNode.setMsg(newNPCMsg);

        return "";
    }

    public String addPMsgInActiveDialogue(String pMsg, UUID playerID) {
        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);
        if (fetchedDialogue == null) {
            return "No active dialogue!";
        }

        if (pMsg == null || pMsg == "") {
            return "Can't set empty message!";
        }

        fetchedDialogue.addPMsgToCurrent(pMsg);

        return "";
    }

    public String delPMsgInActiveDialogue(int givenPNum, UUID playerID) {

        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);
        if (fetchedDialogue == null) {
            return "No active dialogue!";
        }


        return fetchedDialogue.delPStatement(givenPNum);
    }

    public String selectForActiveDialogue(int selection, UUID playerID) {
        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);
        if (fetchedDialogue == null) {
            return "No active dialogue!";
        }

        return fetchedDialogue.selPStatement(selection - 1);

    }


    public String addNPCMsgToPMsgForActiveDialogue(String msg, int selectedPMsg, UUID playerID) {
        DialogueTree fetchedDialogue = getEditingDialogueForPlayer(playerID);
        if (fetchedDialogue == null) {
            return "No active dialogue!";
        }

        fetchedDialogue.addNPCMsgToPMsg(selectedPMsg - 1, msg);

        return "";
    }

    public String startConversationWithNPCForPlayer(String npcName, UUID playerID) {
        // get the dialogue ID for that npc, if it exists
        String returnedDialogueID = main.getInstance().getNPCMgr().getDialogueIDForNPCWithName(npcName);
        String errString = "This person is unable to talk at the moment.";

        if (returnedDialogueID == null) {
            return errString;
        }
        else {
            // get conversation from UUID. For now, just pull it from the closedDialogues.
            DialogueTree fetchedDialogue = closedDialogues.get(returnedDialogueID);
            if (fetchedDialogue == null) {
                return errString;
            }

            // new plan:
            // there will be only once instance of each speaking dialogue.
            // these dialogues have bookmarks for each player, holding their place in the tree.
            // TD: if the player has a convo with another NPC, end it

            speakingDialogues.put(playerID, fetchedDialogue);
            return fetchedDialogue.playerStartedConversation(playerID);

        }
    }

    public String closeActivePlayerConversation(UUID playerID) {
        if (speakingDialogues.get(playerID) != null) {
            speakingDialogues.remove(playerID);
        }
        return "";
    }

    public String saveAndCloseActiveDialogueForPlayer(UUID playerID) {
        // copy the active dialogue to editing dialogues, and remove it from editing hashmap
        if (editingDialogues.get(playerID) == null) {
            return "No active dialogue to close";
        }

        DialogueTree copiedDTree = DialogueTree.copyTree(editingDialogues.get(playerID));
        String DUniqueName = playerID + "_" + copiedDTree.name;
        closedDialogues.put(DUniqueName, copiedDTree);

        if (copiedDTree == null) {
            System.out.println("DEBUG: Error: tried to copy tree, result was null");
        }

        editingDialogues.put(playerID, null);
        System.out.println("Unique name for tree being saved is: " + DUniqueName);
        return "Saved and closed dialogue " + copiedDTree.name + ".";
    }

    // TD: rename to playerSelectedPMsg
    public String playerSelected(Integer selection, UUID playerID) {
        if (playerID == null) return "";
        DialogueTree fetchedDialogue = speakingDialogues.get(playerID);

        if (fetchedDialogue == null) {
            return "You are not currently speaking to anyone.";
        }


        return speakingDialogues.get(playerID).playerSelectedDialogueOption(selection, playerID);

    }


    public void saveClosedDialogues() {
        // see if the dialogues folder exists. If not, try to create it

        File folder = new File("Dialogues");
        if (!(folder.exists() && folder.isDirectory())) {
            // try to create Folder, then proceed
            if (folder.mkdir()) {
                System.out.println("Dialogues folder does not exist, created");
            } else
            {
                System.out.println("Could not create Dialogues folder. Unable to save dialogues");
                return;
            }
        }

        // create gson object
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResult;
        // new code: save all dialogues
        Iterator it = closedDialogues.entrySet().iterator();
        // below copied from: https://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
        // modifying to add all these map items to a list, and saving the array as JSON
        DialogueTree[] treeList = new DialogueTree [closedDialogues.size()];
        int i = 0;

        while (it.hasNext() && i < treeList.length) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            DialogueTree thisDialogue = (DialogueTree) pair.getValue();


            FileWriter fwriter;
            try {fwriter = new FileWriter("Dialogues/" + (pair.getKey() + "_" + ((DialogueTree) pair.getValue()).name));}
            catch (IOException e) {
                System.out.println("Error: Could not open dialogue file to write" + e);
                return;
            }

            jsonResult = "";
            try {
                jsonResult = gson.toJson(thisDialogue);
                System.out.println("Attempted to convert tree to JSON: \n" + jsonResult);
            } catch (Exception e) {
                System.out.println("Error: could not convert dialogue tree to json. " + e);
                return;
            }

            try {fwriter.write(jsonResult);
                fwriter.close();}
            catch (IOException e) {
                System.out.println("Error: could not write json dialogue to file. " + e);
                return;
            }

            i++;
        }
        }



    // TD: refactor
    public void loadDialogues() {
        Gson gsonObj = new Gson();

        // get all files
        File folder = new File("Dialogues/");
        if (folder == null) {
            System.out.println("FluidStories: Error: could not open Dialogues folder.");
        }
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files)
        {
            // check that the filename format is correct
            String fileName = file.getName();
            DialogueTree parsedData;
            if (fileName.contains("_")) {
                String[] sections = fileName.split("_"); // hold onto this for now
                if (sections.length > 2 || fileName.contains(".")) {
                    // invalid format, skip processing this file
                    System.out.println("DEBUG: checked file: " + fileName + "does not fit format");
                    continue;
                }
                // load file contents, parse with gson
                String jsonStr;
                System.out.println("Loading dialogue file: " + fileName);
                try {jsonStr = new String(Files.readAllBytes(Paths.get("Dialogues/" + fileName))); }
                catch (IOException e) {
                    System.out.println("Error: could not read dialogue file. " + e);
                    return;
                }

                try {parsedData = gsonObj.fromJson(jsonStr, DialogueTree.class);}
                catch (Exception e) {
                    System.out.println("Error: could not parse dialogue data; " + e);
                    return;
                }

                // add newly parsed dialogue to active npc dialogues
                closedDialogues.put(fileName, parsedData);

            }

        }


/*
        // --------------below is copied from NPC data. delete after completing
        //  create object to hold array
        NPCData[] parsedData;


        // get json string from file
        String jsonStr;
        try {jsonStr = new String(Files.readAllBytes(Paths.get("npcs"))); }
        catch (IOException e) {
            System.out.println("Error: could not read file to load dialogue. " + e);
            return;
        }

        try {parsedData = gsonObj.fromJson(jsonStr, NPCData[].class);}
        catch (Exception e) {
            System.out.println("Error: could not parse npc data");
            return;
        }

        if (parsedData == null) {
            System.out.println("Did not load any NPCs from JSON file.");
            return;
        }

        for (int i = 0; i < parsedData.length; i++) {
            System.out.println("DEBUG: loading NPC: " + parsedData[i].name);
            activeNPCs.put(parsedData[i].name, parsedData[i]);
        }


        // if there's a problem with an entry, skip it and go to the next one
*/
    }

}