package us.nyrene.fluidstories;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin {
    CmdFS fsCommand = new CmdFS();

    @Override
    public void onEnable() {
        this.getCommand("spawnnpc").setExecutor(new CmdSpawnNPC());
        this.getCommand("setnpcdesc").setExecutor(new CmdSetNPCDesc());
        this.getCommand("looknpc").setExecutor(new CmdLookNPC());
        this.getCommand("fs").setExecutor(fsCommand);
        //this.getCommand("talknpc").setExecutor()
    }

    @Override
    public void onDisable(){

    }
}
