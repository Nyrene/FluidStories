package us.nyrene.fluidstories;

//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
//import org.bukkit.entity.Villager;
import org.bukkit.command.CommandExecutor;

public class CmdSetNPCDesc implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender pSender, Command cmd, String strLbl, String[] args) {

        if (pSender instanceof Player) {
            //writer.sendMessage("This is the command!");

            // check that the args are correct: 2+ arguments, the name of the NPC and the description
            if (args.length == 0 || args.length < 2) {
                return false;
            }

            Player writer = (Player) pSender; // use try statement here, and send any caught errors to writer
            String newDesc = "";
            for (int i = 1; i < args.length; i++) {
                newDesc = newDesc + " " + args[i];
            }

            DError errInst = new DError();
            main.getInstance().getNPCMgr().setNPCDescriptionForPlayer(args[0], newDesc, writer.getUniqueId(), errInst);
            if (errInst.type != DErrorType.NOERROR) {
                writer.sendMessage(errInst.msg);
            } else {
                writer.sendMessage("Description set!");
            }

            return true;
        }

        return false;
    }
}