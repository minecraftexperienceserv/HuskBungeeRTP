package me.william278.huskbungeertp.config;

import java.util.HashSet;

public record Group(String groupId, String groupDatabaseTableName, int coolDownTimeMinutes,
                    HashSet<Server> servers) {

    public String getGroupId() { return groupId; }

    public String getGroupDatabaseTableName() {
        return groupDatabaseTableName;
    }

    public int getCoolDownTimeMinutes() {
        return coolDownTimeMinutes;
    }

    public HashSet<Server> getServers() {
        return servers;
    }

    public record Server(String name, HashSet<String> worlds) {

        public String getName() {
            return name;
        }

        public HashSet<String> getWorlds() {
            return worlds;
        }
    }
}
