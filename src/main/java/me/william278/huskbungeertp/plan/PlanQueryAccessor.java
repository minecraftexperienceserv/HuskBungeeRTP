package me.william278.huskbungeertp.plan;

import com.djrapitops.plan.query.CommonQueries;
import com.djrapitops.plan.query.QueryService;
import me.william278.huskbungeertp.HuskBungeeRTP;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public record PlanQueryAccessor(QueryService queryService) {

    public PlanQueryAccessor(QueryService queryService) {
        this.queryService = queryService;

        ensureDBSchemaMatch();
    }

    private void ensureDBSchemaMatch() {
        CommonQueries queries = queryService.getCommonQueries();
        if (
                !queries.doesDBHaveTable("plan_sessions")
                        || !queries.doesDBHaveTableColumn("plan_sessions", "uuid")
        ) {
            throw new IllegalStateException("Different table schema");
        }
    }

    public HashMap<String, Long> getPlayTimes() {
        Set<UUID> UUIDList = queryService.getCommonQueries().fetchServerUUIDs();
        final HashMap<String, Long> serverPlayTimes = new HashMap<>();
        for (UUID serverUUID : UUIDList) {
            String getServerName = "SELECT * FROM plan_servers WHERE `uuid`=?;";
            String serverName = queryService.query(getServerName, preparedStatement -> {
                preparedStatement.setString(1, serverUUID.toString());
                try (ResultSet set = preparedStatement.executeQuery()) {
                    return set.next() ? set.getString("name") : null;
                }
            });
            String selectSessionsPerDay = "SELECT SUM(" + "session_end" + '-' + "session_start" + ") as playtime" +
                    " FROM plan_sessions" +
                    " WHERE server_uuid" + "=?" +
                    " AND session_end" + ">=?" +
                    " AND session_start" + "<=?";
            long playtime = queryService.query(selectSessionsPerDay, statement -> {
                statement.setString(1, serverUUID.toString());
                statement.setLong(2, (System.currentTimeMillis() - (TimeUnit.DAYS.toMillis(HuskBungeeRTP.getSettings().getAveragePlayerCountDays()))));
                statement.setLong(3, System.currentTimeMillis());
                try (ResultSet set = statement.executeQuery()) {
                    return set.next() ? set.getLong("playtime") : -1L;
                }
            });
            serverPlayTimes.put(serverName, playtime);
        }

        return serverPlayTimes;
    }
}