package me.william278.huskbungeertp.mysql;

import me.william278.huskbungeertp.HuskBungeeRTP;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {
    protected HuskBungeeRTP plugin;

    public final static String DATA_POOL_NAME = "HuskBungeeRTPHikariPool";

    public Database(HuskBungeeRTP instance) {
        plugin = instance;
    }

    public abstract Connection getConnection() throws SQLException;

    public abstract void load();

    public abstract void close();

    public final int hikariMaximumPoolSize = HuskBungeeRTP.getSettings().getHikariMaximumPoolSize();
    public final int hikariMinimumIdle = HuskBungeeRTP.getSettings().getHikariMinimumIdle();
    public final long hikariMaximumLifetime = HuskBungeeRTP.getSettings().getHikariMaximumLifetime();
    public final long hikariKeepAliveTime = HuskBungeeRTP.getSettings().getHikariKeepAliveTime();
    public final long hikariConnectionTimeOut = HuskBungeeRTP.getSettings().getHikariConnectionTimeOut();
}
