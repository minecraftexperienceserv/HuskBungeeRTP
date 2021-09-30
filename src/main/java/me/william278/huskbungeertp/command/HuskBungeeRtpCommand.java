package me.william278.huskbungeertp.command;

import de.themoep.minedown.MineDown;
import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.MessageManager;
import me.william278.huskbungeertp.config.Group;
import me.william278.huskbungeertp.config.Settings;
import me.william278.huskbungeertp.plan.PlanDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class HuskBungeeRtpCommand implements CommandExecutor {

    private static final HuskBungeeRTP plugin = HuskBungeeRTP.getInstance();
    private static final StringBuilder PLUGIN_INFORMATION = new StringBuilder()
            .append("[HuskBungeeRTP](#00fb9a bold) [| Version ").append(plugin.getDescription().getVersion()).append("](#00fb9a)\n")
            .append("[").append(plugin.getDescription().getDescription()).append("](gray)\n")
            .append("[• Author:](white) [William278](gray show_text=&7Click to pay a visit open_url=https://youtube.com/William27528)\n")
            .append("[• Support Discord:](white) [[Link]](#00fb9a show_text=&7Click to join open_url=https://discord.gg/tVYhJfyDWG)");


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "about", "info" -> sender.spigot().sendMessage(new MineDown(PLUGIN_INFORMATION.toString()).toComponent());
                case "groups" -> {
                    sender.spigot().sendMessage(new MineDown("[HuskBungeeRTP](#00fb9a bold) [| Available groups:](#00fb9a)").toComponent());
                    HashSet<Group> groups = HuskBungeeRTP.getSettings().getGroups();
                    if (groups.isEmpty()) {
                        sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [There are no RTP destination groups currently configured!](#ff7e5e)").toComponent());
                        return true;
                    }
                    for (Group group : groups) {
                        StringJoiner joiner = new StringJoiner("\n");
                        for (Group.Server server : group.getServers()) {
                            StringJoiner worldJoiner = new StringJoiner(", ");
                            for (String worldName : server.getWorlds()) {
                                worldJoiner.add(worldName);
                            }
                            joiner.add("&f• &7" + server.getName() + "\\(" + worldJoiner + "\\)");
                        }
                        sender.spigot().sendMessage(new MineDown("[" + group.groupId() + "](#00fb9a show_text=&#00fb9a&Group ID) [•](#262626) [" + group.getGroupDatabaseTableName() + "](gray show_text=&#00fb9a&Group database table name) [•](#262626) [⌚" + group.getCoolDownTimeMinutes() + "m](gray show_text=&#00fb9a&Group cooldown time) [•](#262626) [[ⓘ Servers]](white show_text=&#00fb9a&Servers & worlds:\b&f" + joiner + ")").toComponent());
                    }
                }
                case "plan" -> {
                    if (PlanDataManager.usePlanIntegration()) {
                        HashMap<String, Long> planPlayTimes = PlanDataManager.getPlanPlayTimes();
                        if (planPlayTimes == null) {
                            sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [Failed to retrieve the Plan play times.](#ff7e5e)").toComponent());
                            return true;
                        }
                        if (planPlayTimes.isEmpty()) {
                            sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [Could not find any servers correctly configured with Plan!](#ff7e5e)").toComponent());
                            return true;
                        }

                        sender.spigot().sendMessage(new MineDown("[HuskBungeeRTP](#00fb9a bold) [| Total Plan play times for servers \\(in ticks, over " + HuskBungeeRTP.getSettings().getPlanAveragePlayerCountDays() + "d\\):](#00fb9a)").toComponent());
                        for (String serverId : planPlayTimes.keySet()) {
                            long playTime = planPlayTimes.get(serverId);
                            sender.spigot().sendMessage(new MineDown("[•](#262626) [" + serverId + ":](#00fb9a show_text=&#00fb9a&ID of the server) [⌚" + playTime + " ticks](white show_text=&#00fb9a&The total play time, in ticks, according to the Plan database)").toComponent());
                        }
                    } else {
                        sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [The Player Analytics \\(Plan\\) integration is currently disabled.](#ff7e5e)").toComponent());
                    }
                }
                case "playercounts" -> {
                    if (HuskBungeeRTP.getSettings().getLoadBalancingMethod() == Settings.LoadBalancingMethod.PLAYER_COUNTS) {
                        HashMap<String,Integer> playerCounts = HuskBungeeRTP.serverPlayerCounts;
                        if (playerCounts == null) {
                            sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [Failed to retrieve the player counts.](#ff7e5e)").toComponent());
                            return true;
                        }
                        if (playerCounts.isEmpty()) {
                            sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [Could not find any servers; are your groups setup in your config?](#ff7e5e)").toComponent());
                            return true;
                        }
                        sender.spigot().sendMessage(new MineDown("[HuskBungeeRTP](#00fb9a bold) [| Current player counts on your network):](#00fb9a)").toComponent());
                        for (String serverId : playerCounts.keySet()) {
                            int playerCount = playerCounts.get(serverId);
                            sender.spigot().sendMessage(new MineDown("[•](#262626) [" + serverId + ":](#00fb9a show_text=&#00fb9a&ID of the server) [☻" + playerCount + " players](white show_text=&#00fb9a&The number of players on this server)").toComponent());
                        }
                    } else {
                        sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [You do not have player count mode set for the load balancer](#ff7e5e)").toComponent());
                    }
                }
                case "reload" -> {
                    HuskBungeeRTP.reloadConfigFile();
                    sender.spigot().sendMessage(new MineDown("[HuskBungeeRTP](#00fb9a bold) [| Reloaded config and message files.](#00fb9a)").toComponent());
                }
            }
        } else {
            MessageManager.sendMessage(sender, "error_invalid_syntax", command.getUsage());
        }
        return true;
    }

    public static class HuskBungeeRtpTabCompleter implements TabCompleter {

        private static final String[] commandTabArgs = {"about", "groups", "plan", "playercounts", "reload"};

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            if (command.getPermission() != null) {
                if (!sender.hasPermission(command.getPermission())) {
                    return Collections.emptyList();
                }
            }
            if (args.length == 0 || args.length == 1) {
                final List<String> tabCompletions = new ArrayList<>();
                StringUtil.copyPartialMatches(args[0], Arrays.asList(commandTabArgs), tabCompletions);
                Collections.sort(tabCompletions);
                return tabCompletions;
            }
            return Collections.emptyList();
        }
    }
}
