package me.william278.huskbungeertp.command;

import me.william278.huskbungeertp.randomtp.RtpHandler;
import me.william278.huskbungeertp.randomtp.RtpProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RtpCommand implements CommandExecutor {

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                RtpHandler.processRtp(player, new RtpProfile());
            } else {
                sender.sendMessage("error_console_player_specify");
            }
            return true;
        }
        // todo more handling :)
        return true;
    }
}
