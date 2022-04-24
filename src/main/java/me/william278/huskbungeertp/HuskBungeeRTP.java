package me.william278.huskbungeertp;

import me.william278.huskbungeertp.command.HuskBungeeRtpCommand;
import me.william278.huskbungeertp.command.RtpCommand;
import me.william278.huskbungeertp.config.Group;
import me.william278.huskbungeertp.config.Settings;
import me.william278.huskbungeertp.jedis.RedisMessage;
import me.william278.huskbungeertp.jedis.RedisMessenger;
import me.william278.huskbungeertp.mysql.DataHandler;
import me.william278.huskbungeertp.randomtp.processor.AbstractRtp;
import me.william278.huskbungeertp.randomtp.processor.DefaultRtp;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class HuskBungeeRTP extends JavaPlugin {

    private static HuskBungeeRTP instance;

    public static HuskBungeeRTP getInstance() {
        return instance;
    }

    private static Settings settings;

    public static Settings getSettings() {
        return settings;
    }

    private void setSettings(Configuration config) {
        settings = new Settings(config);
    }

    public static Logger rtpLogger;

    public static HashMap<String,Integer> serverPlayerCounts = new HashMap<>();
    public static void updateServerPlayerCounts() {
        for (String server : getSettings().getAllServerIds()) {
            RedisMessenger.publish(new RedisMessage(server, RedisMessage.RedisMessageType.GET_PLAYER_COUNT,
                    getSettings().getServerId() + "#" + Instant.now().getEpochSecond()));
        }
    }
    public static HashSet<String> getServerIdsWithFewestPlayers(Collection<Group.Server> servers) {
        HashSet<String> lowestIdServers = new HashSet<>();
        int lowestPlayerCount = Integer.MAX_VALUE;
        for (Group.Server server : servers) {
            if (serverPlayerCounts.containsKey(server.getName())) {
                String serverName = server.getName();
                int playerCount = serverPlayerCounts.get(serverName);
                if (playerCount < lowestPlayerCount) {
                    lowestPlayerCount = playerCount;
                    lowestIdServers.clear();
                    lowestIdServers.add(serverName);
                } else if (playerCount == lowestPlayerCount) {
                    lowestIdServers.add(serverName);
                }
            } else {
                HuskBungeeRTP.getInstance().getLogger().warning("A server in a RTP group failed to return play count data.");
            }
        }
        return lowestIdServers;
    }

    private static AbstractRtp abstractRtp;

    public static AbstractRtp getAbstractRtp() {
        return abstractRtp;
    }

    private void setAbstractRtp() {
        abstractRtp = new DefaultRtp();
        abstractRtp.initialize();
    }

    private void setupLogger() {
        rtpLogger = Logger.getLogger("RTPLogger");
        FileHandler loggerFile;

        try {
            loggerFile = new FileHandler(getDataFolder().getAbsolutePath() + File.separator + "RTPLogger.log");
            rtpLogger.addHandler(loggerFile);
            SimpleFormatter formatter = new SimpleFormatter();
            loggerFile.setFormatter(formatter);
            rtpLogger.info("Server " + getSettings().getServerId() + " initialized.");
        } catch (SecurityException e) {
            getLogger().log(Level.WARNING, "A logger SecurityException has occurred", e);
        } catch (IOException i) {
            getLogger().log(Level.WARNING, "A logger IOException has occurred", i);
        }
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    public static void reloadConfigFile() {
        HuskBungeeRTP instance = HuskBungeeRTP.getInstance();
        instance.reloadConfig();
        instance.saveDefaultConfig();
        instance.setSettings(instance.getConfig());
        MessageManager.loadMessages();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Load settings and messages
        reloadConfigFile();

        // Load database
        DataHandler.loadDatabase(getInstance());

        // Set RTP handler
        setAbstractRtp();

        // Register command
        Objects.requireNonNull(getCommand("rtp")).setExecutor(new RtpCommand());
        Objects.requireNonNull(getCommand("rtp")).setTabCompleter(new RtpCommand.RtpTabCompleter());
        Objects.requireNonNull(getCommand("huskbungeertp")).setExecutor(new HuskBungeeRtpCommand());
        Objects.requireNonNull(getCommand("huskbungeertp")).setTabCompleter(new HuskBungeeRtpCommand.HuskBungeeRtpTabCompleter());

        // Register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // fetch player counts
        switch (getSettings().getLoadBalancingMethod()) {
            case PLAYER_COUNTS -> updateServerPlayerCounts();
        }

        // Jedis subscriber initialisation
        RedisMessenger.subscribe();


        // Setup debug logger
        if (getSettings().doDebugLogging()) {
            setupLogger();
        }

        // Log to console
        getLogger().info("Successfully enabled HuskBungeeRTP v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // Close the database connection
        DataHandler.closeDatabase();

        // Plugin shutdown logic
        getLogger().info("Disabled HuskBungeeRTP v" + getDescription().getVersion());
    }
}
