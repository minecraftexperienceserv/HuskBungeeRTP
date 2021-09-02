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
import java.util.UUID;

public class RtpHandler {

    private static final HuskBungeeRTP plugin = HuskBungeeRTP.getInstance();

    public static void processRtp(Player player, RtpProfile profile) {
        final UUID uuid = player.getUniqueId();
        final boolean canBypassCoolDown = player.hasPermission("huskrtp.bypass_cooldown");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final DataHandler.CoolDownResponse coolDownResponse = DataHandler.getPlayerCoolDown(player.getUniqueId(), profile.getDestinationGroup());
            if (coolDownResponse.isInCoolDown() && !canBypassCoolDown) {
                if (HuskBungeeRTP.getSettings().isUseLastRtpLocationOnCoolDown()) {
                    TeleportationPoint lastRtpPosition = DataHandler.getPlayerLastRtpPosition(uuid, profile.getDestinationGroup());
                    if (lastRtpPosition != null) {
                        if (coolDownResponse.timeLeft() <= 60) {
                            MessageManager.sendMessage(player, "last_rtp_cooldown_seconds", Long.toString(coolDownResponse.timeLeft()));
                        } else {
                            MessageManager.sendMessage(player, "last_rtp_cooldown_minutes", Integer.toString((int) (coolDownResponse.timeLeft() / 60)));
                        }
                        HuskHomesExecutor.teleportPlayer(player, lastRtpPosition);
                        return;
                    }
                }
                if (coolDownResponse.timeLeft() <= 60) {
                    MessageManager.sendMessage(player, "error_cooldown_seconds", Long.toString(coolDownResponse.timeLeft()));
                } else {
                    MessageManager.sendMessage(player, "error_cooldown_minutes", Integer.toString((int) (coolDownResponse.timeLeft() / 60)));
                }
                return;
            }
            MessageManager.sendMessage(player, "processing_rtp");

            Group.Server targetServer = determineTargetServer(profile.getDestinationGroup().getServers());
            String targetWorld = determineTargetWorld(targetServer);

            if (targetServer.getName().equals(HuskBungeeRTP.getSettings().getServerId())) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    World targetLocalWorld = Bukkit.getWorld(targetWorld);
                    if (targetLocalWorld == null) {
                        targetLocalWorld = player.getWorld();
                    }
                    final TeleportationPoint targetPoint = new TeleportationPoint(HuskBungeeRTP.getAbstractRtp().getRandomLocation(targetLocalWorld), targetServer.getName());
                    HuskHomesExecutor.teleportPlayer(player, targetPoint);

                    // Apply cool down
                    if (!canBypassCoolDown) {
                        DataHandler.setPlayerOnCoolDown(uuid, profile.getDestinationGroup(), targetPoint);
                    }
                });
            } else {
                // Cross server RTP time!
                RedisMessenger.publish(new RedisMessage(targetServer.getName(), RedisMessage.RedisMessageType.REQUEST_RANDOM_LOCATION,
                        uuid + "#" + HuskBungeeRTP.getSettings().getServerId() + "#" + targetWorld + "#" + "TARGET_BIOME" + "#" + profile.getDestinationGroup().getGroupId())); //todo target biome!
            }
        });
    }

    private static Group.Server determineTargetServer(HashSet<Group.Server> servers) {
        // todo PlanIntegration!
        final ArrayList<Group.Server> shuffledServers = new ArrayList<>(servers);
        Collections.shuffle(shuffledServers);
        return shuffledServers.get(0);
    }

    private static String determineTargetWorld(Group.Server server) {
        final ArrayList<String> worlds = new ArrayList<>(server.getWorlds());
        Collections.shuffle(worlds);
        return worlds.get(0);
    }

}
