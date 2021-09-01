package me.william278.huskbungeertp;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;

public class MessageManager {

    private static final Map<String, String> messages = new HashMap<>();

    private static final HuskBungeeRTP plugin = HuskBungeeRTP.getInstance();

    // Create a new file, at the pointer specified
    private static void createFile(File f) {
        try {
            if (!f.createNewFile()) {
                Bukkit.getLogger().severe("Failed to create messages.yml file!");
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("An error occurred while creating the messages.yml file!");
            e.printStackTrace();
        }
    }

    // Save the config file specified to the file at the pointer specified
    private static void saveFile(FileConfiguration config, File f) {
        try {
            config.save(f);
        } catch (IOException e) {
            Bukkit.getLogger().severe("An error occurred while saving the messages.yml file!");
        }
    }

    // (Re-)Load the messages file with the correct language
    public static void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder() + File.separator + "messages.yml");

        if (!messagesFile.exists()) {
            createFile(messagesFile);
        }
        FileConfiguration config = loadConfiguration(messagesFile);
        config.options().header(
                """
                         ------------------------------\s
                        |    HuskBungeeRTP Messages    |
                        |    Developed by William278   |
                         ------------------------------\s
                        Change the appearance/text of messages in the plugin using this config.\s
                        This config makes use of MineDown formatting, with extensive support for custom colors & formats.\s
                        For formatting help, see the MineDown wiki: https://github.com/Phoenix616/MineDown""");
        InputStream defaultMessageFile = plugin.getResource("messages.yml");
        if (defaultMessageFile != null) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultMessageFile, StandardCharsets.UTF_8));
            config.setDefaults(yamlConfiguration);
            config.options().copyHeader(true).copyDefaults(true);
        }
        saveFile(config, messagesFile);
        messages.clear();
        for (String message : config.getKeys(false)) {
            messages.put(message, StringEscapeUtils.unescapeJava(config.getString(message)));
        }
    }

    // Send a message to the correct channel
    private static void sendMessage(CommandSender sender, String messageID, String... placeholderReplacements) {
        String message = getRawMessage(messageID);

        // Don't send empty messages
        if (StringUtils.isEmpty(message)) {
            return;
        }

        int replacementIndexer = 1;

        // Replace placeholders
        for (String replacement : placeholderReplacements) {
            String replacementString = "%" + replacementIndexer + "%";
            message = message.replace(replacementString, replacement);
            replacementIndexer = replacementIndexer + 1;
        }

        // Convert to baseComponents[] via MineDown formatting and send
        sender.spigot().sendMessage(new MineDown(message).replace().toComponent());
    }

    // Send a message with no placeholder parameters
    public static void sendMessage(CommandSender sender, String messageID) {
        String message = getRawMessage(messageID);

        // Don't send empty messages
        if (StringUtils.isEmpty(message)) {
            return;
        }

        // Convert to baseComponents[] via MineDown formatting and send
        sender.spigot().sendMessage(new MineDown(message).replace().toComponent());
    }

    public static String getRawMessage(String messageID) {
        return messages.get(messageID);
    }

    public static String getRawMessage(String messageID, String... placeholderReplacements) {
        String message = messages.get(messageID);
        int replacementIndexer = 1;

        // Replace placeholders
        for (String replacement : placeholderReplacements) {
            String replacementString = "%" + replacementIndexer + "%";
            message = message.replace(replacementString, replacement);
            replacementIndexer = replacementIndexer + 1;
        }
        return message;
    }

}
