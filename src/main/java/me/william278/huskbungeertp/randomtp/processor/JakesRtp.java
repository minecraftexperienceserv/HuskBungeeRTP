package me.william278.huskbungeertp.randomtp.processor;

import biz.donvi.jakesRTP.JakesRtpPlugin;
import biz.donvi.jakesRTP.RandomTeleporter;
import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.config.Group;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class JakesRtp extends AbstractRtp {

    private RandomTeleporter randomTeleporter;

    @Override
    public void initialize() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("JakesRTP");
        if (!(plugin instanceof JakesRtpPlugin)) {
            return;
        }
        randomTeleporter = ((JakesRtpPlugin) plugin).getRandomTeleporter();
        try {
            for (Group.Server server : HuskBungeeRTP.getSettings().getThisServerGroup().getServers()) {
                if (server.getName().equals(HuskBungeeRTP.getSettings().getServerId())) {
                    for (String worldName : server.getWorlds()) {
                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            randomTeleporter.fillQueue(randomTeleporter.getRtpSettingsByWorld(world));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            HuskBungeeRTP.getInstance().getLogger().log(Level.SEVERE, "An exception occurred caching JakesRTP random locations!", e);
        }
    }

    @Override
    public Location getRandomLocation(World world) {
        Location location = world.getSpawnLocation();
        try {
            location = randomTeleporter.getRtpLocation(randomTeleporter.getRtpSettingsByWorld(world), world.getSpawnLocation(), true);
        } catch (Exception e) {
            HuskBungeeRTP.getInstance().getLogger().log(Level.SEVERE, "An exception occurred fetching a random location!", e);
        }
        return location;
    }

}
