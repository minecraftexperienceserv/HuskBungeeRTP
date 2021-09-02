package me.william278.huskbungeertp.mysql;

import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.config.Group;
import me.william278.huskhomes2.teleport.points.TeleportationPoint;
import org.bukkit.Bukkit;

import java.sql.*;
import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;

public class DataHandler {

    private static HuskBungeeRTP plugin;
    private static Database database;

    private static Connection getConnection() {
        return database.getConnection();
    }

    public static void loadDatabase(HuskBungeeRTP instance) {
        database = new MySQL(instance);
        database.load();
        plugin = instance;
    }

    public static void addPlayerIfNotExist(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Connection connection = getConnection();
            try (PreparedStatement checkIfPlayerExist = connection.prepareStatement(
                    "SELECT * FROM " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " WHERE `user_uuid`=? LIMIT 1;")) {
                checkIfPlayerExist.setString(1, uuid.toString());
                final ResultSet playerExistResultSet = checkIfPlayerExist.executeQuery();
                // If the player does not exist yet
                if (!playerExistResultSet.next()) {
                    try (PreparedStatement createPlayerStatement = connection.prepareStatement(
                            "INSERT INTO " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " (`user_uuid`) VALUES (?)")) {
                        createPlayerStatement.setString(1, uuid.toString());
                        createPlayerStatement.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An SQL exception has occurred", e);
            }
        });
    }

    /*
    Not thread safe, perform inside a RunTaskAsynchronously!
     */
    public static CoolDownResponse getPlayerCoolDown(UUID uuid, Group group) {
        boolean isPlayerOnCoolDown = false;
        long coolDownTimeLeft = 0L;
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM " + group.getGroupDatabaseTableName() + " WHERE `player_id`=(SELECT `id` FROM " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " WHERE `user_uuid`=? LIMIT 1) LIMIT 1;")) {
            preparedStatement.setString(1, uuid.toString());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Timestamp lastRtpTimestamp = resultSet.getTimestamp("last_rtp");
                coolDownTimeLeft = lastRtpTimestamp.toInstant().getEpochSecond() + (60L * group.coolDownTimeMinutes()) - Instant.now().getEpochSecond();
                if (coolDownTimeLeft <= 0) {
                    try (PreparedStatement deletePlayerCoolDownStatement = connection.prepareStatement(
                            "DELETE FROM " + group.getGroupDatabaseTableName() + " WHERE `player_id`=(SELECT `id` FROM " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " WHERE `user_uuid`=? LIMIT 1) LIMIT 1;")) {
                        deletePlayerCoolDownStatement.setString(1, uuid.toString());
                        deletePlayerCoolDownStatement.executeUpdate();
                    }
                } else {
                    isPlayerOnCoolDown = true;
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An SQL exception has occurred", e);
        }
        return new CoolDownResponse(isPlayerOnCoolDown, coolDownTimeLeft);
    }

    /*
        Not thread safe, perform inside a RunTaskAsynchronously!
     */
    public static TeleportationPoint getPlayerLastRtpPosition(UUID uuid, Group group) {
        TeleportationPoint point = null;
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM " + group.getGroupDatabaseTableName() + " WHERE `player_id`=(SELECT `id` FROM " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " WHERE `user_uuid`=? LIMIT 1) LIMIT 1;")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                point = new TeleportationPoint(resultSet.getString("dest_world"),
                        resultSet.getDouble("dest_x"),
                        resultSet.getDouble("dest_y"),
                        resultSet.getDouble("dest_z"),
                        0F, 0F,
                        resultSet.getString("dest_server"));
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An SQL exception has occurred", e);
        }
        return point;
    }

    public static void setPlayerOnCoolDown(UUID uuid, Group group, TeleportationPoint point) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Connection connection = getConnection();

            try (PreparedStatement isPlayerNotAlreadySetCheck = connection.prepareStatement(
                    "SELECT * FROM " + group.getGroupDatabaseTableName() + " WHERE `player_id`=(SELECT `id` FROM " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " WHERE `user_uuid`=?);")) {
                isPlayerNotAlreadySetCheck.setString(1, uuid.toString());
                ResultSet playerSetResultSet = isPlayerNotAlreadySetCheck.executeQuery();
                if (!playerSetResultSet.next()) {
                    try (PreparedStatement getPlayerIdStatement = connection.prepareStatement(
                            "SELECT * FROM " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " WHERE `user_uuid`=? LIMIT 1;")) {
                        getPlayerIdStatement.setString(1, uuid.toString());
                        ResultSet playerIdSet = getPlayerIdStatement.executeQuery();
                        if (playerIdSet.next()) {
                            int playerId = playerIdSet.getInt("id");
                            try (PreparedStatement preparedStatement = connection.prepareStatement(
                                    "INSERT INTO " + group.getGroupDatabaseTableName() + " (`player_id`,`dest_world`,`dest_x`,`dest_y`,`dest_z`,`dest_server`) VALUES(?,?,?,?,?,?);")) {
                                preparedStatement.setInt(1, playerId);
                                preparedStatement.setString(2, point.getWorldName());
                                preparedStatement.setDouble(3, point.getX());
                                preparedStatement.setDouble(4, point.getY());
                                preparedStatement.setDouble(5, point.getZ());
                                preparedStatement.setString(6, point.getServer());
                                preparedStatement.executeUpdate();
                            }
                        }
                    }

                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An SQL exception has occurred", e);
            }
        });
    }

    public record CoolDownResponse(boolean isInCoolDown, long timeLeft) {
    }

}
