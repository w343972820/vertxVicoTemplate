package vicoMain;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import utils.JdbcUtils;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Sql_Config_Log_Web_Time  extends AbstractVerticle {
    InternalLogger logger = Log4JLoggerFactory.getInstance(Sql_Config_Log_Web_Time.class);
    JDBCClient jdbcClient;
    JsonObject jsonConfig;

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
        jdbcClient = new JdbcUtils(vertx).getDbClient();
        RedisOptions config = new RedisOptions().setHost("192.168.1.69").setPort(6379).setAuth("oQFMvr*QgoX4vznj");
        redisClient = RedisClient.create(vertx, config);
        VicoDao vicoDao = new VicoDao(jdbcClient);
        //新建post请求,在chuLiWeb里面处理,前面是类对象，后面是方法
        router.post("/vicoPostTest").handler(vicoDao::chuLiWeb);
        // 把请求交给路由处理-----
        server.requestHandler(router::accept);
        // 设置端口
        server.listen(8877);
        //设置定时期，定期执行
        this.setStatus1();
        redisTest();
    }

    //设置每分钟执行一次
    private void setStatus1(){
        this.vertx.setPeriodic(60000,handler->{
            lunXunShuJuKu();
        });
    }
    private void redisTest(){
        this.vertx.setPeriodic(60000,handler->{

        });

    }
    private void lunXunShuJuKu() {
        //读取配置，打包成jar后放入服务器，可在服务器同级目录新建conf目录，及conifg.json文件，优先读取文件
        readConfig(vertx);
        System.out.println("测试用...."+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }



    public Future<Boolean> readConfig(Vertx vertx) {
        Future<Boolean> future = Future.future();
        Utils.getConfig(vertx, "conf/config.json").setHandler(handler->{
            if(handler.succeeded()) {
                JsonObject json = handler.result();
                jsonConfig = json;
                System.out.println("json:"+json.encodePrettily());
                future.complete(true);
            }else {
                future.fail("-1");
            }
        });
        return future;
    }
    public static void main(String[] args) {
        //调用vertx
        Vertx.vertx().deployVerticle(new Sql_Config_Log_Web_Time());
    }


}
