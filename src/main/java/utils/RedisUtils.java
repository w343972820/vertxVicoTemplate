package utils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import vicoMain.Sql_Config_Log_Web_Time;



public class RedisUtils {

    InternalLogger logger = Log4JLoggerFactory.getInstance(Sql_Config_Log_Web_Time.class);
    RedisClient redisClient;

    public void setRdisClient(Vertx vertx,Handler<AsyncResult<Boolean>> handle) {
        Future<Boolean> future = Future.future();
        future.setHandler(handle);
        Utils.getConfig(vertx, "conf/config.json").setHandler(handler->{
            if(handler.succeeded()) {
                JsonObject json = handler.result();
                String host=json.getString("redis_host");
                String port=json.getString("redis_port");
                String auth=json.getString("redis_auth");
                RedisOptions options = new RedisOptions();
                options.setHost(host)
                        .setPort(Integer.parseInt(port))
                        .setAuth(auth);
                redisClient = RedisClient.create(vertx, options);
                future.complete();
            }else{
                future.fail("redis连接失败");
                logger.error("redis连接出错:"+handler.cause().getMessage());
            }
        });
    }


    public RedisClient getRedisClient() {
        return this.redisClient;
    }
}
