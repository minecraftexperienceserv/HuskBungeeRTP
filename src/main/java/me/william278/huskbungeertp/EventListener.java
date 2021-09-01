package me.william278.huskbungeertp;

import me.william278.huskbungeertp.mysql.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        DataHandler.addPlayerIfNotExist(p.getUniqueId());
    }

}
