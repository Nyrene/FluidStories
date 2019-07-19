package us.nyrene.fluidstories;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin {
    private static main instance;
    NPCManager npcMgr = new NPCManager();


    CmdFS fsCmd = new CmdFS();
    CmdSetNPCDesc npcDescCmd = new CmdSetNPCDesc();

    @Override
    public void onEnable() {
        this.getCommand("spawnnpc").setExecutor(new CmdSpawnNPC());
        this.getCommand("setnpcdesc").setExecutor(npcDescCmd);
        this.getCommand("looknpc").setExecutor(new CmdLookNPC());
        this.getCommand("fs").setExecutor(fsCmd);
        //this.getCommand("talknpc").setExecutor(talkCmd);
        //this.getCommand("say").setExecutor(sayCmd);
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
}
