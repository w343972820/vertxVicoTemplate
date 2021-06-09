package vicoMain;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.web.RoutingContext;
import utils.JdbcUtils;

import java.util.List;

public class VicoDao {
    InternalLogger logger = Log4JLoggerFactory.getInstance(Sql_Config_Log_Web_Time.class);
    JDBCClient jdbcClient;
    public VicoDao(JDBCClient jdbcClient){
        this.jdbcClient = jdbcClient;
    }
    public void chuLiWeb(RoutingContext req){
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
}
