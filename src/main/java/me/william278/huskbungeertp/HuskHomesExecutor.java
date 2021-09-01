package me.william278.huskbungeertp;

import me.william278.huskhomes2.api.HuskHomesAPI;
import me.william278.huskhomes2.teleport.points.TeleportationPoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class HuskHomesExecutor {

    public static void teleportPlayer(Player player, TeleportationPoint point) {
        if (Bukkit.getPluginManager().getPlugin("HuskHomes") == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not find HuskHomes! Please install HuskHomes!");
            return;
        }
        HuskHomesAPI.getInstance().teleportPlayer(player, point, true);
    }

}
