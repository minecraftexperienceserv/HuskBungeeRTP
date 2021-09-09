package me.william278.huskbungeertp.command;

import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.MessageManager;
import me.william278.huskbungeertp.config.Group;
import me.william278.huskbungeertp.randomtp.RtpHandler;
import me.william278.huskbungeertp.randomtp.RtpProfile;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RtpCommand implements CommandExecutor {

    // Command syntax: /rtp [player] [group] [biome]
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player targetPlayer;
        Group targetGroup = null;
        Biome targetBiome = null;

        if (args.length >= 3) {
            try {
                targetBiome = Biome.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                MessageManager.sendMessage(sender, "error_invalid_biome");
                return true;
            }
        }
        if (args.length >= 2) {
            for (Group group : HuskBungeeRTP.getSettings().getGroups()) {
                if (group.getGroupId().equals(args[1])) {
                    targetGroup = group;
                    break;
                }
            }
            if (targetGroup == null) {
                MessageManager.sendMessage(sender, "error_invalid_group");
                return true;
            }
        } else {
            targetGroup = HuskBungeeRTP.getSettings().getDefaultRtpDestinationGroup();
        }
        if (args.length >= 1) {
            Player exactPlayer = Bukkit.getPlayerExact(args[0]);
            if (exactPlayer != null) {
                targetPlayer = exactPlayer;
            } else {
                MessageManager.sendMessage(sender, "error_invalid_player");
                return true;
            }
        } else if (sender instanceof ConsoleCommandSender) {
            MessageManager.sendMessage(sender, "error_console_player_specify");
            return true;
        } else {
            targetPlayer = (Player) sender;
        }

        if (targetPlayer != sender) {
            MessageManager.sendMessage(sender, "randomly_teleporting_player", targetPlayer.getName());
        }
        if (targetBiome == null) {
            RtpHandler.processRtp(targetPlayer, new RtpProfile(targetGroup));
        } else {
            RtpHandler.processRtp(targetPlayer, new RtpProfile(targetGroup, targetBiome));
        }
        return true;
    }

    public static class RtpTabCompleter implements TabCompleter {
        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (command.getPermission() != null) {
                if (sender.hasPermission(command.getPermission())) {
                    return Collections.emptyList();
                }
            }
            List<String> tabItems = new ArrayList<>();
            final List<String> tabCompletions = new ArrayList<>();
            switch (args.length) {
                case 0:
                case 1:
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        tabItems.add(player.getName());
                    }
                    StringUtil.copyPartialMatches(args[0], tabItems, tabCompletions);
                    break;
                case 2:
                    tabItems.addAll(HuskBungeeRTP.getSettings().getGroupIds());
                    StringUtil.copyPartialMatches(args[1], tabItems, tabCompletions);
                    break;
                case 3:
                    for (Biome biome : Biome.values()) {
                        tabItems.add(biome.name().toLowerCase());
                    }
                    StringUtil.copyPartialMatches(args[2], tabItems, tabCompletions);
                    break;
                default:
                    return Collections.emptyList();
            }
            Collections.sort(tabCompletions);
            return tabCompletions;
        }
    }
}
