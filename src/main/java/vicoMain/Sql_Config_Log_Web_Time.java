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
import io.vertx.ext.web.handler.BodyHandler;
import utils.JdbcUtils;
import utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Sql_Config_Log_Web_Time  extends AbstractVerticle {
    InternalLogger logger = Log4JLoggerFactory.getInstance(Sql_Config_Log_Web_Time.class);
    JDBCClient jdbcClient;
    JsonObject jsonConfig;
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
        //读取配置

        //新建post请求
        router.post("/vicoPostTest").handler(req->{
            //对客户端传入的参数做json处理
            JsonObject resultsJson = JdbcUtils.param(req);
            //判断服务器传过来的json
            String putuuid = resultsJson.getString("putuuid");
            //可以先对putuuid判断，在响应相应的值
            sqlQury(putuuid).setHandler(h->{
                if (h.succeeded()){
                    if (h.result()){
                        req.response()
                                .putHeader("content-type", "application/json")
                                .end(new JsonObject().put("code","2000").put("msg","找到相同:"+putuuid).toString());
                    }else{
                        req.response()
                                .putHeader("content-type", "application/json")
                                .end(new JsonObject().put("code","2000").put("msg","末在数据库内找到相同UUID").toString());
                    }
                }else{
                    System.out.println("数据库查询出错");
                    req.response()
                            .putHeader("content-type", "application/json")
                            .end(new JsonObject().put("code","1000").put("msg","数据库异常"+h.cause()).toString());
                }
            });
        });
        // 把请求交给路由处理-----
        server.requestHandler(router::accept);
        // 设置端口
        server.listen(8877);
        //设置定时期，定期执行
        this.setStatus1();
    }
    //设置每分钟执行一次
    private void setStatus1(){
        this.vertx.setPeriodic(60000,handler->{
            lunXunShuJuKu();
        });
    }
    private void lunXunShuJuKu() {
        //读取配置，打包成jar后放入服务器，可在服务器同级目录新建conf目录，及conifg.json文件，优先读取文件
        readConfig(vertx);
        System.out.println("测试用...."+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }


    //用future返回回调，以保证请求及处理同步问题
    public Future<Boolean> sqlQury(String webUuid){
        Future<Boolean> future = Future.future();
        if (webUuid==null || webUuid==""){
            future.complete(false);
            return future;
        }
        String sqlUid = "select putuuid from tradeWithDraw";
        //String sql = "insert into tradeWithDraw(putuuid,cointypename,putnum,apitype,putaddress,status,createTime) values (?,?,?,?,?,0,?)";
        //String updatesqls = "update tradeWithDraw set status = "+status+",msg=? where putuuid = \'" +txID +"\'";
        //JsonArray params = new JsonArray().add(msg);
        jdbcClient.query(sqlUid,qurSql->{
            if (qurSql.succeeded()){
                // 获取到查询的结果，Vert.x对ResultSet进行了封装
                ResultSet resultSet = qurSql.result();
                // 把ResultSet转为List<JsonObject>形式
                List<JsonObject> resultsList = resultSet.getRows();
                for(JsonObject json:resultsList){
                    System.out.println(json.toString());
                    if (webUuid.equals(json.getString("putuuid"))){
                        future.complete(true);
                        break;
                    }
                }
                if (future.result()==null){
                    future.complete(false);
                }
            }else{
                logger.error(qurSql.cause());
                future.complete(false);
                future.failed();
            }
        });
        return future;
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
