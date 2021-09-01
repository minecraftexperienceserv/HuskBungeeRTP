package me.william278.huskbungeertp.jedis;

import com.sun.source.tree.BreakTree;
import me.william278.huskbungeertp.HuskBungeeRTP;
import redis.clients.jedis.JedisPool;

public class RedisMessenger {

    private static JedisPool jedisPool;

    private static JedisPool getJedisPool() {
        return jedisPool;
    }

    public static void initialize() {
        jedisPool = new JedisPool(HuskBungeeRTP.getSettings().getRedisHost(), HuskBungeeRTP.getSettings().getRedisPort());
    }

}