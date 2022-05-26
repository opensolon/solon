package org.noear.solon.boot.reactornetty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

class RnHttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {


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
        RnHttpContext ctx = new RnHttpContext(request, response, parser);
        ctx.contentType("text/plain;charset=UTF-8");//默认

        if (ServerProps.output_meta) {
            ctx.headerSet("Solon-Boot", XPluginImp.solon_boot_ver());
        }

        Solon.app().tryHandle(ctx);

        if (ctx.status() == 404) {
            return null;  //response.sendNotFound();
        } else {
            return ctx._outputStream.buffer();//response.send(Mono.just(context._outputStream.buffer())).neverComplete();
        }
    }
}
