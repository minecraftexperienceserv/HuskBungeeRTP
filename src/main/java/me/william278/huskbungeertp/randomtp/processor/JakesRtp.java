package me.william278.huskbungeertp.randomtp.processor;

import biz.donvi.jakesRTP.JakesRtpPlugin;
import biz.donvi.jakesRTP.RandomTeleporter;
import me.william278.huskbungeertp.HuskBungeeRTP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.logging.Level;

public class JakesRtp extends AbstractRtp {

    private RandomTeleporter getRandomTeleporter() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("JakesRTP");
        if (!(plugin instanceof JakesRtpPlugin)) {
            return null;
        }
        return ((JakesRtpPlugin) plugin).getRandomTeleporter();
    }

    @Override
    public Location getRandomLocation(World world) {
        Location location = world.getSpawnLocation();
        try {
            location = Objects.requireNonNull(getRandomTeleporter()).getRtpLocation(getRandomTeleporter().getRtpSettingsByWorld(world), world.getSpawnLocation(), true);
        } catch (Exception e) {
            HuskBungeeRTP.getInstance().getLogger().log(Level.SEVERE, "An exception occurred fetching a random location!", e);
        }
        return location;
    }

}
