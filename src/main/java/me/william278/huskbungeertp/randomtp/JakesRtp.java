package me.william278.huskbungeertp.randomtp;

import biz.donvi.jakesRTP.JakesRtpPlugin;
import biz.donvi.jakesRTP.RandomTeleporter;
import biz.donvi.jakesRTP.RtpProfile;
import me.william278.huskbungeertp.HuskBungeeRTP;
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
