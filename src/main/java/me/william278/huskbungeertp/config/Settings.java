package me.william278.huskbungeertp.config;

import org.bukkit.configuration.Configuration;

import java.util.HashSet;
import java.util.Objects;

public class Settings {

    // MySQL credentials
    private final String databaseHost;
    private final int databasePort;
    private final String databaseUsername;
    private final String databasePassword;
    private final String databaseName;
    private final String databasePlayerTableName;
    private final String databaseParams;

    // Jedis credentials
    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;

    // General options
    private final int updatePlanDataHours;
    private final int averagePlayerCountDays;
    private final String serverId;
    private final boolean usePlan;
    private final boolean useLastRtpLocationOnCoolDown;
    private final String defaultRtpDestinationGroup;

    // Group configuration
    private final HashSet<Group> groups;

    public Settings(Configuration config) {
        databaseHost = config.getString("mysql_credentials.host", "localhost");
        databasePort = config.getInt("mysql_credentials.port", 3306);
        databaseUsername = config.getString("mysql_credentials.username", "root");
        databasePassword = config.getString("mysql_credentials.password", "pa55w0rd");
        databaseName = config.getString("mysql_credentials.database", "huskrtp");
        databasePlayerTableName = config.getString("mysql_credentials.player_table_name", "huskrtp_players");
        databaseParams = config.getString("mysql_credentials.params", "?autoReconnect=true&useSSL=false");

        redisHost = config.getString("redis_credentials.host", "localhost");
        redisPort = config.getInt("redis_credentials.port", 3306);
        redisPassword = config.getString("redis_credentials.password", "");

        updatePlanDataHours = config.getInt("update_plan_data_hours", 1);
        averagePlayerCountDays = config.getInt("average_player_count_days", 7);
        serverId = config.getString("this_server_id", "server1");
        usePlan = config.getBoolean("use_plan", false);
        useLastRtpLocationOnCoolDown = config.getBoolean("last_rtp_on_cooldown", true);
        defaultRtpDestinationGroup = config.getString("default_rtp_group", "group1");

        groups = getGroups(config);
    }

    private HashSet<Group> getGroups(Configuration config) {
        HashSet<Group> groups = new HashSet<>();
        for (String groupId : Objects.requireNonNull(config.getConfigurationSection("groups")).getKeys(false)) {
            final String tableName = config.getString("groups." + groupId + ".table_name");
            final int coolDownMinutes = config.getInt("groups." + groupId + ".cooldown_minutes");
            final HashSet<Group.Server> servers = getGroupServers(config, groupId);
            groups.add(new Group(groupId, tableName, coolDownMinutes, servers));
        }
        return groups;
    }

    private HashSet<Group.Server> getGroupServers(Configuration config, String groupId) {
        HashSet<Group.Server> servers = new HashSet<>();
        for (String groupServerId : Objects.requireNonNull(config.getConfigurationSection("groups." + groupId + ".servers_worlds")).getKeys(false)) {
            final HashSet<String> serverWorlds = new HashSet<>(config.getStringList("groups." + groupId + ".servers_worlds." + groupServerId));
            servers.add(new Group.Server(groupServerId, serverWorlds));
        }
        return servers;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabasePlayerTableName() {
        return databasePlayerTableName;
    }

    public String getDatabaseParams() {
        return databaseParams;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public String getRedisPassword() { return redisPassword; }

    public int getUpdatePlanDataHours() { return updatePlanDataHours; }

    public boolean isUsePlan() {
        return usePlan;
    }

    public Group getDefaultRtpDestinationGroup() {
        return getGroupById(defaultRtpDestinationGroup);
    }

    public long getAveragePlayerCountDays() {
        return averagePlayerCountDays;
    }

    public boolean isUseLastRtpLocationOnCoolDown() {
        return useLastRtpLocationOnCoolDown;
    }

    public String getServerId() {
        return serverId;
    }

    public HashSet<Group> getGroups() {
        return groups;
    }

    public HashSet<String> getGroupIds() {
        HashSet<String> groupNames = new HashSet<>();
        for (Group group : groups) {
            groupNames.add(group.getGroupId());
        }
        return groupNames;
    }

    public Group getThisServerGroup() {
        for (Group group : groups) {
            for (Group.Server server : group.getServers()) {
                if (serverId.equals(server.getName())) {
                    return group;
                }
            }
        }
        return null;
    }

    public Group getGroupById(String groupId) {
        for (Group group : groups) {
            if (group.getGroupId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }
}
