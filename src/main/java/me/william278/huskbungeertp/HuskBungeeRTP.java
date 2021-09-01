package me.william278.huskbungeertp;

import me.william278.huskbungeertp.config.Settings;
import me.william278.huskbungeertp.mysql.DataHandler;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

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

        // Load database
        DataHandler.loadDatabase(getInstance());

        // Register events
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
