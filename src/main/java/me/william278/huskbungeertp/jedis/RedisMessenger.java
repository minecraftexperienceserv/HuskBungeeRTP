package me.william278.huskbungeertp.jedis;

import io.papermc.lib.PaperLib;
import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.HuskHomesExecutor;
import me.william278.huskhomes2.teleport.points.TeleportationPoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

public class RedisMessenger {

    private static final String REDIS_CHANNEL = "HuskBungeeRtp";

    public static void subscribe() {
        Jedis jedis = new Jedis(HuskBungeeRTP.getSettings().getRedisHost(), HuskBungeeRTP.getSettings().getRedisPort());
        jedis.connect();
        new Thread(() -> jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (!channel.equals(REDIS_CHANNEL)) {
                    return;
                }
                String[] splitMessage = message.split("£");
                RedisMessage receivedMessage = new RedisMessage(splitMessage[0], RedisMessage.RedisMessageType.valueOf(splitMessage[1]), splitMessage[2]);
                if (!receivedMessage.getTargetServer().equals(HuskBungeeRTP.getSettings().getServerId())) {
                    return;
                }
                Bukkit.getScheduler().runTask(HuskBungeeRTP.getInstance(), () -> handleReceivedMessage(receivedMessage));
            }
        }, REDIS_CHANNEL), "Redis Subscriber").start();
    }

    public static void publish(RedisMessage message) {
        try (Jedis publisher = new Jedis(HuskBungeeRTP.getSettings().getRedisHost(), HuskBungeeRTP.getSettings().getRedisPort())) {
            publisher.connect();
            publisher.publish(REDIS_CHANNEL, message.toString());
        }
    }

    public static void handleReceivedMessage(RedisMessage message) {
        String[] messageData = message.getMessageData().split("#");
        switch (message.getMessageType()) {
            case REQUEST_RANDOM_LOCATION -> {
                // An incoming request that this server find and return a random location
                final UUID sourcePlayerUUID = UUID.fromString(messageData[0]);
                final String sourceServer = messageData[1];
                final String targetWorld = messageData[2];

                //final String targetBiome = messageData[3];
                World world = Bukkit.getWorld(targetWorld);
                if (world != null) {
                    final Location randomLocation = HuskBungeeRTP.getAbstractRtp().getRandomLocation(world);
                    world.getChunkAt(randomLocation); // Load the chunk
                    publish(new RedisMessage(sourceServer, RedisMessage.RedisMessageType.REPLY_RANDOM_LOCATION,
                            sourcePlayerUUID + "#" + HuskBungeeRTP.getSettings().getServerId() + "#" +
                                    world.getName() + "#" + randomLocation.getX() + "#" +
                                    randomLocation.getY() + "#" + randomLocation.getZ()));
                }
            }
            case REPLY_RANDOM_LOCATION -> {
                // A reply from a server that a location has been found and a player should be teleported
                final UUID originPlayerUUID = UUID.fromString(messageData[0]);
                final String sourceServer = messageData[1];
                final String locationWorld = messageData[2];
                final double locationX = Double.parseDouble(messageData[3]);
                final double locationY = Double.parseDouble(messageData[4]);
                final double locationZ = Double.parseDouble(messageData[5]);

                Player player = Bukkit.getPlayer(originPlayerUUID);
                if (player != null) {
                    HuskHomesExecutor.teleportPlayer(player, new TeleportationPoint(
                            locationWorld, locationX, locationY, locationZ, 0F, 0F, sourceServer));
                }
            }
        }
    }

}