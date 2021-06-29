package com.binance.api.client.mainVico;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class WebQianDuan extends AbstractVerticle {
    ArrayList<Long> secretList;
    InternalLogger logger = Log4JLoggerFactory.getInstance(WebQianDuan.class);
    Map<String,Integer> ipMaps= new HashMap<>();
    int updateLabelCount=0;
    @Override
    public void start() throws Exception {
        ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);
        //创建路由
        Router router = Router.router(vertx);
        //解决跨域问题
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.POST).allowedHeader("X-PINGARUNER").allowedHeader("Content-Type")
                .allowedHeader("Access-Control-Allow-Origin").allowedHeader("Access-Control-Allow-Headers"));

        router.get("/VicoTemplates").handler(req -> {
            engine.render(new JsonObject(),"templates/vico.html",bufferAsyncResult->{
                if (bufferAsyncResult.succeeded()){
                    req.response()
                            .putHeader("content-type", "text/html")
                            .end(bufferAsyncResult.result());
                }else{
                    System.out.println("读取模版出错了。。。");
                }

            });
        });
        //如果用户点击，需要修改页面的
        router.route("/updateLabel").handler(req -> {
            //SocketAddress address = req.request().localAddress();
            //String aaa = address.host();
           // logger.info("ip:"+aaa);
            String ip = IPUtils.getIpAddr(req.request());
            logger.info("ip:"+ip);
            if (!ipMaps.containsKey(ip)){
                ipMaps.put(ip,1);
                vertx.setTimer(5000,hand->{
                    ipMaps.remove(ip);

                });
                req.response()
                        .putHeader("content-type", "text/plain")
                        .end("成功fdsafsd。。。"+ip);
            }else{
                if (ipMaps.get(ip)<2){
                    ipMaps.put(ip,ipMaps.get(ip)+1);
                    req.response()
                            .putHeader("content-type", "text/plain")
                            .end("成功fdsafsd。。。"+ip);
                }else{
                    req.response()
                            .putHeader("content-type", "text/plain")
                            .end("请求频繁，等会");
                }
            }
           /* String coin = req.request().getParam("coin");
            String number = req.request().getParam("number");
            JsonObject jspn = new JsonObject();
            jspn.put("coin",coin.toUpperCase());
            jspn.put("number",String.valueOf(Double.parseDouble(number)*2.5));
            logger.info("进来了。。。");
            engine.render(jspn,"templates/vico.html",bufferAsyncResult->{
                if (bufferAsyncResult.succeeded()){
                    logger.info("coin->"+coin+" secret->"+number);
                    req.response()
                            .putHeader("content-type", "text/plain")
                            .end("成功。。。");
                }
            });*/
        });
        router.get("/qwer").handler(req -> {
            String coin = req.request().getParam("coin"); //vert.x获取url参数就这一句
            String number = req.request().getParam("number"); //vert.x获取url参数就这一句
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("coin:"+coin+" number"+number);

        });


        // 创建HttpServer
        HttpServer server = vertx.createHttpServer();

        // 把请求交给路由处理--------------------(1)
        server.requestHandler(router::accept);
        server.listen(8899);
    }


   /* public String honClic(Model model){

    }*/
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new WebQianDuan());
    }
}
