package org.noear.solon.boot.reactornetty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.noear.solon.XApp;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

 class HttpRequestHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
    private XApp app = XApp.global();

     @Override
     public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
         return
         response.sendString(request.receive()
                 .asString()
                 .map(s -> s + ' ' + request.param("param") + '!')
                 .log("http-server"));
     }
 }
