package org.noear.solon.boot.reactornetty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.http.*;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.http.HttpOperations;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

class RnHttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
    private XApp app = XApp.global();

    @Override
    public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {

        ByteBufAllocator bba = ((HttpOperations) request).alloc();

        DefaultFullHttpRequest req = new DefaultFullHttpRequest(
                request.version(),
                request.method(),
                request.uri(),
                bba.buffer(),
                request.requestHeaders(),
                new DefaultHttpHeaders(true));


        return applyDo(request, response, new HttpRequestParser(req));
    }

    protected Publisher<Void> applyDo(HttpServerRequest request, HttpServerResponse response, HttpRequestParser parser) {
        RnHttpContext context = new RnHttpContext(request, response, parser);
        context.contentType("text/plain;charset=UTF-8");//默认
        if (XServerProp.output_meta) {
            context.headerSet("solon.boot", XPluginImp.solon_boot_ver());
        }

        try {
            app.handle(context);
        } catch (Throwable ex) {
            ex.printStackTrace();

            context.status(500);
            context.setHandled(true);
            context.output(XUtil.getFullStackTrace(ex));
        }

        if (context.status() == 404) {
            return response.sendNotFound();
        } else {
            return response.send(Mono.just(context._outputStream.buffer())).neverComplete();
        }
    }
}
