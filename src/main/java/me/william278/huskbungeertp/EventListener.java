package me.william278.huskbungeertp;

import me.william278.huskbungeertp.config.Settings;
import me.william278.huskbungeertp.mysql.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        DataHandler.addPlayerIfNotExist(p.getUniqueId());
        if (HuskBungeeRTP.getSettings().getLoadBalancingMethod() == Settings.LoadBalancingMethod.PLAYER_COUNTS) {
            HuskBungeeRTP.updateServerPlayerCounts();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (HuskBungeeRTP.getSettings().getLoadBalancingMethod() == Settings.LoadBalancingMethod.PLAYER_COUNTS) {
            HuskBungeeRTP.updateServerPlayerCounts();
        }
    }

}
