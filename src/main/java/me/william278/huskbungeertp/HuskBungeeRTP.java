package me.william278.huskbungeertp;

import me.william278.huskbungeertp.command.RtpCommand;
import me.william278.huskbungeertp.config.Settings;
import me.william278.huskbungeertp.jedis.RedisMessenger;
import me.william278.huskbungeertp.mysql.DataHandler;
import me.william278.huskbungeertp.randomtp.processor.AbstractRtp;
import me.william278.huskbungeertp.randomtp.processor.DefaultRtp;
import me.william278.huskbungeertp.randomtp.processor.JakesRtp;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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

    private static AbstractRtp abstractRtp;
    public static AbstractRtp getAbstractRtp() { return abstractRtp; }
    private void setAbstractRtp() {
        if (Bukkit.getPluginManager().getPlugin("JakesRTP") != null) {
            abstractRtp = new JakesRtp();
        } else {
            abstractRtp = new DefaultRtp();
        }
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Load settings
        reloadConfig();
        saveDefaultConfig();
        setSettings(getConfig());

        // Load messages
        MessageManager.loadMessages();

        // Load database
        DataHandler.loadDatabase(getInstance());

        // Set RTP handler
        setAbstractRtp();

        // Register command
        Objects.requireNonNull(getCommand("rtp")).setExecutor(new RtpCommand());

        // Register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        // Jedis subscriber initialisation
        RedisMessenger.subscribe();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
