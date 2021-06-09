package utils;

import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.Properties;

public class RedisUtils {
    private static final RedisOptions options = new RedisOptions();
    private static final Properties config = new Properties();

    static {
        try {
            config.load(RedisUtils.class.getClassLoader().getResourceAsStream("redis.properties"));

            options.setHost(config.getProperty("host"))
                    .setPort(Integer.parseInt(config.getProperty("port")))
                    .setAuth(config.getProperty("auth"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RedisClient getRedisClient(Vertx vertx) {
        RedisClient redisClient = RedisClient.create(vertx, options);
        return redisClient;
    }
}
