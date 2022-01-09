package me.william278.huskbungeertp.mysql;

import com.zaxxer.hikari.HikariDataSource;
import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.config.Group;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;

public class MySQL extends Database {

    public static final String PLAYER_TABLE_CREATION_STATEMENT = "CREATE TABLE IF NOT EXISTS " + HuskBungeeRTP.getSettings().getDatabasePlayerTableName() + " ("
            + "`id` integer AUTO_INCREMENT PRIMARY KEY,"
            + "`user_uuid` char(36) NOT NULL UNIQUE"
            + ");";

    public static final String GROUP_TABLE_CREATION_STATEMENT = "CREATE TABLE IF NOT EXISTS {0} ("
            + "`player_id` integer AUTO_INCREMENT PRIMARY KEY,"
            + "`last_rtp` timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),"
            + "`dest_world` tinytext NOT NULL,"
            + "`dest_x` double NOT NULL,"
            + "`dest_y` double NOT NULL,"
            + "`dest_z` double NOT NULL,"
            + "`dest_server` tinytext NOT NULL"
            + ");";

    final String host = HuskBungeeRTP.getSettings().getDatabaseHost();
    final int port = HuskBungeeRTP.getSettings().getDatabasePort();
    final String database = HuskBungeeRTP.getSettings().getDatabaseName();
    final String username = HuskBungeeRTP.getSettings().getDatabaseUsername();
    final String password = HuskBungeeRTP.getSettings().getDatabasePassword();
    final String params = HuskBungeeRTP.getSettings().getDatabaseParams();

    private HikariDataSource dataSource;

    public MySQL(HuskBungeeRTP instance) {
        super(instance);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void load() {
        // Create new HikariCP data source
        final String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + params;
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);

        dataSource.setUsername(username);
        dataSource.setPassword(password);

        // Set various additional parameters
        dataSource.setMaximumPoolSize(hikariMaximumPoolSize);
        dataSource.setMinimumIdle(hikariMinimumIdle);
        dataSource.setMaxLifetime(hikariMaximumLifetime);
        dataSource.setKeepaliveTime(hikariKeepAliveTime);
        dataSource.setConnectionTimeout(hikariConnectionTimeOut);
        dataSource.setPoolName(DATA_POOL_NAME);

        // Create tables
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                // Create the player table
                statement.execute(PLAYER_TABLE_CREATION_STATEMENT);

                for (Group group : HuskBungeeRTP.getSettings().getGroups()) {
                    // Replace {0} with the group name and execute
                    statement.execute(MessageFormat.format(GROUP_TABLE_CREATION_STATEMENT, group.getGroupDatabaseTableName()));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred creating tables on the MySQL database: ", e);
        }
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
