package org.noear.solon.boot.reactornetty;

import io.netty.buffer.ByteBuf;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

class HttpRequestHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
    private XApp app = XApp.global();

     @Override
     public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {

         NettyHttpContext context = new NettyHttpContext(request,response);
         context.contentType("text/plain;charset=UTF-8");//默认
         if(XServerProp.output_meta) {
             context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
         }

         try {
             app.handle(context);
         }catch (Throwable ex){
             ex.printStackTrace();

             context.status(500);
             context.setHandled(true);
             context.output(XUtil.getFullStackTrace(ex));
         }

         if(context.status() == 404){
             return response.sendNotFound();
         }else {
             return response.send(Mono.just(context._outputStream.buffer()));
         }
     }
 }
