package me.totalfreedom.totalfreedommod.commands;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.util.DepreciationAggregator;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = false)
@CommandParameters(description = "Remove all blocks of a certain type in the radius of certain players.", usage = "/<command> <block> [radius (default=50)] [player]")
public class Command_ro extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1 || args.length > 3)
        {
            return false;
        }

        final List<Material> materials = new ArrayList<Material>();

        for (String materialName : StringUtils.split(args[0], ","))
        {
            Material fromMaterial = Material.matchMaterial(materialName);
            if (fromMaterial == null)
            {
                try
                {
                    fromMaterial = DepreciationAggregator.getMaterial(Integer.parseInt(materialName));
                }
                catch (NumberFormatException ex)
                {
                }
            }

            if (fromMaterial == null)
            {
                playerMsg("Invalid block: " + materialName, ChatColor.RED);
                return true;
            }

            materials.add(fromMaterial);
        }

        int radius = 20;
        if (args.length >= 2)
        {
            try
            {
                radius = Math.max(1, Math.min(50, Integer.parseInt(args[1])));
            }
            catch (NumberFormatException ex)
            {
                playerMsg("Invalid radius: " + args[1], ChatColor.RED);
                return true;
            }
        }

        final Player targetPlayer;
        if (args.length == 3)
        {
            targetPlayer = getPlayer(args[2]);
            if (targetPlayer == null)
            {
                playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }
        }
        else
        {
            targetPlayer = null;
        }

        final String names = StringUtils.join(materials, ", ");

        World adminWorld = null;
        try
        {
            adminWorld = plugin.wm.adminworld.getWorld();
        }
        catch (Exception ex)
        {
        }

        int affected = 0;
        if (targetPlayer == null)
        {
            FUtil.adminAction(sender.getName(), "Removing all " + names + " within " + radius + " blocks of all players... Brace for lag!", false);

            for (final Player player : server.getOnlinePlayers())
            {
                if (player.getWorld() == adminWorld)
                {
                    continue;
                }

                for (final Material material : materials)
                {
                    affected += FUtil.replaceBlocks(player.getLocation(), material, Material.AIR, radius);
                }
            }
        }
        else
        {
            if (targetPlayer.getWorld() != adminWorld)
            {
                for (Material material : materials)
                {
                    FUtil.adminAction(sender.getName(), "Removing all " + names + " within " + radius + " blocks of " + targetPlayer.getName(), false);
                    affected += FUtil.replaceBlocks(targetPlayer.getLocation(), material, Material.AIR, radius);
                }
            }
        }

        FUtil.adminAction(sender.getName(), "Remove complete! " + affected + " blocks removed.", false);

        return true;
    }
}