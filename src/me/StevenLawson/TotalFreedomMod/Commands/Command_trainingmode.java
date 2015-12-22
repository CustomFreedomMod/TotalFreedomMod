package me.StevenLawson.TotalFreedomMod.Commands;
//This is not a copy of FOPM's /trainingmode, this is a copy of the original /adminmode but edited only a bit.
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Close server to non-superadmins.", usage = "/<command> [activate | deactive]")
public class Command_trainingmode extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("deactivate"))
        {
            TFM_ConfigEntry.ADMIN_ONLY_MODE.setBoolean(false);
            TFM_Util.adminAction(sender.getName(), "Opening the server to all players.", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("activate"))
        {
            TFM_ConfigEntry.ADMIN_ONLY_MODE.setBoolean(true);
            TFM_Util.adminAction(sender.getName(), "Closing the server to non-superadmins to train new admins", true);
            for (Player player : server.getOnlinePlayers())
            {
                if (!TFM_AdminList.isSuperAdmin(player))
                {
                    player.kickPlayer("Server is now closed to non-superadmins for training of new admins");
                }
            }
            return true;
        }

        return false;
    }
}
