package me.william278.huskbungeertp.randomtp;

import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.HuskHomesExecutor;
import me.william278.huskbungeertp.MessageManager;
import me.william278.huskbungeertp.config.Group;
import me.william278.huskbungeertp.jedis.RedisMessage;
import me.william278.huskbungeertp.jedis.RedisMessenger;
import me.william278.huskbungeertp.mysql.DataHandler;
import me.william278.huskhomes2.teleport.points.TeleportationPoint;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class RtpHandler {

    private static final HuskBungeeRTP plugin = HuskBungeeRTP.getInstance();

    public static void processRtp(Player player, RtpProfile profile) {
        MessageManager.sendMessage(player, "processing_rtp");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (DataHandler.isPlayerOnCoolDown(player.getUniqueId(), profile.getDestinationGroup()) && !player.hasPermission("huskrtp.bypass_cooldown")) {
                MessageManager.sendMessage(player, "error_cooldown");
                return;
            }

            Group.Server targetServer = determineTargetServer(profile.getDestinationGroup().getServers());
            String targetWorld = determineTargetWorld(targetServer);

            if (targetServer.getName().equals(HuskBungeeRTP.getSettings().getServerId())) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    World targetLocalWorld = Bukkit.getWorld(targetWorld);
                    if (targetLocalWorld == null) {
                        targetLocalWorld = player.getWorld();
                    }
                    HuskHomesExecutor.teleportPlayer(player, new TeleportationPoint(HuskBungeeRTP.getAbstractRtp().getRandomLocation(targetLocalWorld), targetServer.getName()));
                });
            } else {
                // Cross server RTP time!
                RedisMessenger.publish(new RedisMessage(targetServer.getName(), RedisMessage.RedisMessageType.REQUEST_RANDOM_LOCATION,
                        player.getUniqueId() + "#" + HuskBungeeRTP.getSettings().getServerId() + "#" + targetWorld + "#" + "TARGET_BIOME")); //todo target biome!
            }

            // Apply cool down
            DataHandler.setPlayerOnCoolDown(player.getUniqueId(), profile.getDestinationGroup());
        });
    }

    private static Group.Server determineTargetServer(HashSet<Group.Server> servers) {
        // todo PlanIntegration!
        final ArrayList<Group.Server> shuffledServers = new ArrayList<>(servers);
        Collections.shuffle(shuffledServers);
        return shuffledServers.get(0);
    }

    private static  String determineTargetWorld(Group.Server server) {
        final ArrayList<String> worlds = new ArrayList<>(server.getWorlds());
        Collections.shuffle(worlds);
        return worlds.get(0);
    }

}
