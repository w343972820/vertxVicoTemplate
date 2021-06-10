package utils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.RoutingContext;
import vicoMain.Sql_Config_Log_Web_Time;

import java.net.URLDecoder;

public class JdbcUtils {

    private JDBCClient dbClient;
    InternalLogger logger = Log4JLoggerFactory.getInstance(Sql_Config_Log_Web_Time.class);

    public void setDbClient(Vertx vertx,Handler<AsyncResult<Boolean>> handle) {
        // 构造数据库的连接信息
        JsonObject dbConfig = new JsonObject();
        Future<Boolean> future = Future.future();
        future.setHandler(handle);
        Utils.getConfig(vertx, "conf/config.json").setHandler(handler->{
            if(handler.succeeded()) {
                JsonObject json = handler.result();
                String url=json.getString("db_url");
                String driver_class=json.getString("db_driver_class");
                String user=json.getString("db_user");
                String password=json.getString("db_password");
                //内网
                dbConfig.put("url", url);
                dbConfig.put("driver_class", driver_class);
                dbConfig.put("user", user);
                dbConfig.put("password", password);
                dbClient = JDBCClient.createShared(vertx, dbConfig);
                future.complete();
                logger.info("数据库连接成功："+dbConfig.toString());
            }else {
                future.fail("数据库连接失败");
                logger.error("数据库连接出错:"+handler.cause().getMessage());
            }
        });
        //外网
 /*       dbConfig.put("url", "jdbc:mysql://192.168.1.69:3306/wallet_exchange?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
        dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
        dbConfig.put("user", "viconame");
        dbConfig.put("password", "123456");*/
    }
    // 提供一个公共方法来获取客户端
    public JDBCClient getDbClient() {
        return dbClient;
    }
    public static JsonObject param(RoutingContext context) {
        String par = new String(context.getBody().getBytes());
        if(StringUtil.isBlank(par)){
            return null;
        }
        par = par.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        String param="";
        try {
            param = URLDecoder.decode(par, "UTF-8");
        } catch (Exception e) {
            return null;
        }
        JsonObject json = new JsonObject(param);
        return json;
    }


}
