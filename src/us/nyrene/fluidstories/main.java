package us.nyrene.fluidstories;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin {
    private static main instance;
    NPCManager npcMgr = new NPCManager();
    DialogueManager dialogueManager = new DialogueManager();


    CmdFS fsCmd = new CmdFS();
    CmdSetNPCDesc npcDescCmd = new CmdSetNPCDesc();
    CmdRemoveNPC rmNPCCMD = new CmdRemoveNPC();
    CmdTalkNPC talkCmd = new CmdTalkNPC();
    CmdSay sayCmd = new CmdSay();
    CmdSetNPCDialogue setDialogueCmd = new CmdSetNPCDialogue();

    @Override
    public void onEnable() {
        this.getCommand("spawnnpc").setExecutor(new CmdSpawnNPC());
        this.getCommand("setnpcdesc").setExecutor(npcDescCmd);
        this.getCommand("looknpc").setExecutor(new CmdLookNPC());
        this.getCommand("fs").setExecutor(fsCmd);
        this.getCommand("removenpc").setExecutor(rmNPCCMD);
        this.getCommand("talknpc").setExecutor(talkCmd);
        this.getCommand("say").setExecutor(sayCmd);
        this.getCommand("setnpcdialogue").setExecutor(setDialogueCmd);
    }

    @Override
    public void onDisable(){

    }

    @Override
    public void onLoad() {
        instance = this;
    }

    public static main getInstance() {
        return instance;
    }


    public NPCManager getNPCMgr() {
        return npcMgr;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }
}
