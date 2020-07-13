package org.noear.solon.boot.reactornetty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.*;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XMonitor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.ByteBufMono;
import reactor.netty.http.HttpOperations;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

class RnHttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
    private XApp app = XApp.global();

    @Override
    public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {

        List<ByteBuf>  ary  = new ArrayList<>();

        request.receive().aggregate().map(m->{
            ary.add(m);
            return m;
        }).subscribe().dispose();


//        Mono<ByteBuf> mono = request.receive()
//                .aggregate()
//                .map(byteBuf -> applyDo(request, response,byteBuf));



        return response.send(Mono.just(applyDo(request, response, ary.get(0))));

//        ByteBufAllocator bba = ((HttpOperations) request).alloc();
//
//
//        return applyDo(request, response, new HttpRequestParser(req));
    }

    protected ByteBuf applyDo(HttpServerRequest request, HttpServerResponse response, ByteBuf byteBuf){
        DefaultFullHttpRequest req = new DefaultFullHttpRequest(
                request.version(),
                request.method(),
                request.uri(),
                byteBuf,
                request.requestHeaders(),
                EmptyHttpHeaders.INSTANCE);

        return applyDo(request,response, new HttpRequestParser(req));
    }

    protected ByteBuf applyDo(HttpServerRequest request, HttpServerResponse response, HttpRequestParser parser) {
        RnHttpContext context = new RnHttpContext(request, response, parser);
        context.contentType("text/plain;charset=UTF-8");//默认
        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            app.handle(context);
        } catch (Throwable ex) {
            XMonitor.sendError(context,ex);

            context.status(500);
            context.setHandled(true);
            context.output(XUtil.getFullStackTrace(ex));
        }

        if (context.status() == 404) {
            return null;  //response.sendNotFound();
        } else {
            return context._outputStream.buffer();//response.send(Mono.just(context._outputStream.buffer())).neverComplete();
        }
    }
}
