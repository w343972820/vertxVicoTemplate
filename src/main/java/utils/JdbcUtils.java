package utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.RoutingContext;

import java.net.URLDecoder;

public class JdbcUtils {

    private JDBCClient dbClient;

    public JdbcUtils(Vertx vertx) {

        // 构造数据库的连接信息
        JsonObject dbConfig = new JsonObject();
        //内网
        dbConfig.put("url", "jdbc:mysql://192.168.1.69:3307/test?useUnicode=true&characterEncoding=utf8");
        //dbConfig.put("driver_class", "com.mysql.jdbc.Driver");
        dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
        dbConfig.put("user", "root");
        dbConfig.put("password", "DEbZYnu8?KaCtZCZ");
        //外网
 /*       dbConfig.put("url", "jdbc:mysql://192.168.1.69:3306/wallet_exchange?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
        dbConfig.put("driver_class", "com.mysql.cj.jdbc.Driver");
        dbConfig.put("user", "viconame");
        dbConfig.put("password", "123456");*/

        // 创建客户端
        dbClient = JDBCClient.createShared(vertx, dbConfig);
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
