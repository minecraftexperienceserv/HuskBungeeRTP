package me.william278.huskbungeertp.command;

import de.themoep.minedown.MineDown;
import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.MessageManager;
import me.william278.huskbungeertp.config.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.StringJoiner;

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
                case "about" -> sender.spigot().sendMessage(new MineDown(PLUGIN_INFORMATION.toString()).toComponent());
                case "groups" -> {
                    sender.spigot().sendMessage(new MineDown("[HuskBungeeRTP](#00fb9a bold) [Available groups:](#00fb9a)").toComponent());
                    HashSet<Group> groups = HuskBungeeRTP.getSettings().getGroups();
                    if (groups.isEmpty()) {
                        sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [There are no RTP destination groups currently configured!](#ff7e5e)").toComponent());
                        return true;
                    }
                    for (Group group : groups) {
                        StringJoiner joiner = new StringJoiner("\n");
                        for (Group.Server server : group.getServers()) {
                            joiner.add("&f• &7" + server.getName());
                        }
                        sender.spigot().sendMessage(new MineDown("[" + group.groupId() + "](#00fb9a show_text=&#00fb9a&Group ID) [•](#262626) [" + group.getGroupDatabaseTableName() + "](#00fb9a show_text=&#00fb9a&Group database table name) [⌚" + group.getCoolDownTimeMinutes() + "m](#00fb9a show_text=&#00fb9a&Group cooldown time) [•](#262626) + [[View servers...]](white show_text=&#00fb9a&ⓘ Server List:&f" + joiner + ")").toComponent());
                    }
                }
                case "plan" -> {
                    if (HuskBungeeRTP.usePlanIntegration()) {
                        HashMap<String,Long> planPlayTimes = HuskBungeeRTP.getPlanPlayTimes();
                        if (planPlayTimes == null) {
                            sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [Failed to retrieve the Plan play times.](#ff7e5e)").toComponent());
                            return true;
                        }
                        if (planPlayTimes.isEmpty()) {
                            sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [Could not find any servers correctly configured with Plan!](#ff7e5e)").toComponent());
                            return true;
                        }

                        sender.spigot().sendMessage(new MineDown("[HuskBungeeRTP](#00fb9a bold) [Current Plan servers:](#00fb9a)").toComponent());
                        for (String serverId : planPlayTimes.keySet()) {
                            Long playTime = planPlayTimes.get(serverId);
                            sender.spigot().sendMessage(new MineDown("[•](#262626) [" + serverId + ":](#00fb9a show_text=&#00fb9a&The Server ID) [⌚" + playTime + "t](white show_text=&#00fb9a&The total play time, in ticks, according to the Plan database)").toComponent());
                        }
                    } else {
                        sender.spigot().sendMessage(new MineDown("[Error:](#ff3300) [The Player Analytics \\(Plan\\) integration is currently disabled.](#ff7e5e)").toComponent());
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
}
