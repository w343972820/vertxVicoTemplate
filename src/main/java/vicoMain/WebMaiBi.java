package com.binance.api.client.mainVico;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

import java.util.ArrayList;


public class WebMaiBi extends AbstractVerticle {
    InternalLogger logger = Log4JLoggerFactory.getInstance(ConfigSellCoin.class);
    ArrayList<Long> secretList;
    @Override
    public void start() throws Exception {
        GoogleYanZheng googleYanZheng = new GoogleYanZheng();
        ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);

        ShiXIanLei shiXIanLei = new ShiXIanLei(JieWeiOrderByTime.apiKey,JieWeiOrderByTime.secret);
        // 创建HttpServer
        HttpServer server = vertx.createHttpServer();
        //创建路由
        Router router = Router.router(vertx);

        //解决跨域问题
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.POST).allowedHeader("X-PINGARUNER").allowedHeader("Content-Type")
                .allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers"));

        router.get("/buyOrSellCoin").handler(req -> {
            engine.render(new JsonObject(),"templates/index.html", bufferAsyncResult->{
                if (bufferAsyncResult.succeeded()){
                    req.response()
                            .putHeader("content-type", "text/html")
                            .end(bufferAsyncResult.result());
                }else{
                    System.out.println("读取模版出错了。。。");
                }

            });
        });

        router.route("/toSellCoin").handler(req -> {
            String coin = req.request().getParam("coin"); //vert.x获取url参数就这一句
            String number = req.request().getParam("number"); //vert.x获取url参数就这一句
            String secret = req.request().getParam("secret"); //vert.x获取url参数就这一句
            System.out.println("secret:"+secret);
            secretList = googleYanZheng.getGoogleYanZheng("JN7YX2PEDZQGLSAY");
            boolean secretIsSecucss=false;
            //System.out.println(secretList.get(5)+"或"+secretList.get(6));
            if (Long.valueOf(secret).compareTo(secretList.get(5))==0 || Long.valueOf(secret).compareTo(secretList.get(6))==0){
                secretIsSecucss=true;
            }
            //如果秘钥正确则去卖币
            if (secretIsSecucss){
                String jiaoYiBi=coin.toUpperCase();
                String ltcQuntity=number;
                String coinBi="USDT";  //用于交易的币，也就是钱
                JieWeiOrderByTime.markSellLuoji(shiXIanLei,jiaoYiBi.toUpperCase(),coinBi,ltcQuntity);
                req.response()
                        .putHeader("content-type", "text/plain;charset=UTF-8")
                        .end("卖出->"+jiaoYiBi+":"+ltcQuntity+" ->执行成功");
                logger.info(jiaoYiBi.toUpperCase()+":"+ltcQuntity);
            }else{
                req.response()
                        .putHeader("content-type", "text/plain;charset=UTF-8")
                        .end("秘钥错误。。");
            }
        });
        router.route("/queryQuanlity").handler(req -> {
            String coin = req.request().getParam("coin");
            String secret = req.request().getParam("secret");
            secretList = googleYanZheng.getGoogleYanZheng("JN7YX2PEDZQGLSAY");
            boolean secretIsSecucss=false;
            if (Long.valueOf(secret).compareTo(secretList.get(5))==0 || Long.valueOf(secret).compareTo(secretList.get(6))==0){
                secretIsSecucss=true;
            }
            if (secretIsSecucss){
                String jiaoYiCounts = shiXIanLei.getCounts(coin.toUpperCase());
                req.response()
                        .putHeader("content-type", "text/plain;charset=UTF-8")
                        .end(coin+":"+jiaoYiCounts);
                logger.info(coin.toUpperCase()+":"+jiaoYiCounts);
            }else {
                req.response()
                        .putHeader("content-type", "text/plain;charset=UTF-8")
                        .end("秘钥错误。。");
            }
        });
        // 把请求交给路由处理--------------------(1)
        server.requestHandler(router::accept);
        server.listen(8888);
    }
    public static void main(String[] args) {
        //调用vertx
        Vertx.vertx().deployVerticle(new WebMaiBi());
    }

}
