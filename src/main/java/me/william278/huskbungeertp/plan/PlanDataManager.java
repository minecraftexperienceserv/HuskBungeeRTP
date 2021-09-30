package me.william278.huskbungeertp.plan;

import me.william278.huskbungeertp.HuskBungeeRTP;
import me.william278.huskbungeertp.config.Group;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class PlanDataManager {
    private static long lastPlanFetch;
    private static boolean usePlan = false;
    private static HashMap<String, Long> planPlayTimes = new HashMap<>();

    public static HashMap<String,Long> getPlanPlayTimes() {
        return planPlayTimes;
    }

    public static boolean usePlanIntegration() {
        return usePlan;
    }

    public static HashSet<String> getServerIdsWithLowestPlayTime(Collection<Group.Server> servers) {
        HashSet<String> lowestIdServers = new HashSet<>();
        long lowestPlayTime = Long.MAX_VALUE;
        for (Group.Server server : servers) {
            if (planPlayTimes.containsKey(server.getName())) {
                String serverName = server.getName();
                long playTime = planPlayTimes.get(serverName);
                if (playTime < lowestPlayTime) {
                    lowestPlayTime = playTime;
                    lowestIdServers.clear();
                    lowestIdServers.add(serverName);
                } else if (playTime == lowestPlayTime) {
                    lowestIdServers.add(serverName);
                }
            } else {
                HuskBungeeRTP.getInstance().getLogger().warning("A server in a RTP group failed to return Plan playtime data.");
            }
        }
        return lowestIdServers;
    }

    public static void updatePlanPlayTimes() {
        try {
            Optional<PlanQueryAccessor> planHook = new PlanHook().hookIntoPlan();
            planHook.ifPresent(hook -> {
                usePlan = true;
                planPlayTimes = hook.getPlayTimes();
                HuskBungeeRTP.getInstance().getLogger().info("Fetched latest playtime data from Plan");
            });
            lastPlanFetch = Instant.now().getEpochSecond();
        } catch (NoClassDefFoundError ignored) {
        }
    }

    public static void fetchPlanIfNeeded() {
        if ((lastPlanFetch + (HuskBungeeRTP.getSettings().getPlanUpdateFrequencyMinutes() * 60L)) <= Instant.now().getEpochSecond()) {
            updatePlanPlayTimes();
        }
    }
}
