package vicoMain;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.RedisClient;
import utils.JdbcUtils;
import utils.RedisUtils;
public class Sql_Config_Log_Web_Time  extends AbstractVerticle {
    InternalLogger logger = Log4JLoggerFactory.getInstance(Sql_Config_Log_Web_Time.class);
    JDBCClient jdbcClient;
    RedisClient redisClient;
    @Override
    public void start() throws Exception {
        // 创建HttpServer
        HttpServer server = vertx.createHttpServer();
        //创建路由
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        //设置请求参数为body内带json
        router.route().consumes("application/json");
        router.route().produces("application/json");
        JdbcUtils jdbcUtils = new JdbcUtils();
        logger.info("jdbcClient1:"+jdbcClient);
        jdbcUtils.setDbClient(vertx,hands->{
            if (hands.succeeded()){
                jdbcClient = jdbcUtils.getDbClient();
                logger.info("jdbcClient2:"+jdbcClient);

                VicoDao vicoDao = new VicoDao(jdbcClient);
                //新建post请求,在chuLiWeb里面处理,前面是类对象，后面是方法
                router.post("/vicoPostTest").handler(vicoDao::chuLiWeb);
            }
        });
        //redisClient= RedisUtils.getRedisClient(vertx);
        RedisUtils redisUtils = new RedisUtils();
        redisUtils.setRdisClient(vertx,hands->{
                if (hands.succeeded()){
                    redisClient = redisUtils.getRedisClient();
                }else{
                    logger.error("redis连接失败："+hands.cause().getMessage());
                }
        });
        // 把请求交给路由处理-----
        server.requestHandler(router::accept);
        // 设置端口
        server.listen(8877);
        //设置定时期，定期执行
        this.redisTest();
    }
    private void redisTest(){
        this.vertx.setPeriodic(60000,handler->{
            if (redisClient!=null){
                redisClient.hget("Bittrex","btcusdt",res->{
                    if (res.succeeded()) {
                        System.out.println("ok:"+res.result());
                    }else {
                        logger.error(res.cause().getMessage());
                        res.cause().printStackTrace();
                    }
                });
            }else{
                logger.error("redis为空。。。");
            }
        });
    }
    public static void main(String[] args) {
        //调用vertx
        Vertx.vertx().deployVerticle(new Sql_Config_Log_Web_Time());
    }


}
