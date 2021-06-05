package org.noear.solon.cloud.extend.zipkin.filter;

import brave.Span;
import brave.Tracer;
import brave.propagation.Propagation;
import org.noear.snack.ONode;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.FilterChain;
import zipkin2.Endpoint;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author wenj91
 * @Description:
 * @date 2018/6/22 10:44
 */
public class ZipkinHelper {

     static final Propagation.Setter<Map<String, String>, String> SETTER =
            new Propagation.Setter<Map<String, String>, String>() {
                @Override
                public void put(Map<String, String> carrier, String key, String value) {
                    carrier.put(key, value);
                }
                @Override
                public String toString() {
                    return ONode.stringify(this);
                }
            };
     static final Propagation.Getter<Map<String, String>, String> GETTER =
            new Propagation.Getter<Map<String, String>, String>() {
                @Override
                public String get(Map<String, String> carrier, String key) {
                    return carrier.get(key);
                }

                @Override
                public String toString() {
                    return ONode.stringify(this);
                }
            };

    static void buildSpan(Span span, Span.Kind kind, InetSocketAddress remoteAddress, String service, String path){
        if (!span.isNoop()) {
            span.kind(kind).start();
            span.kind(kind);
            span.name(service + ":/" + path);
            Endpoint.Builder remoteEndpoint = Endpoint.newBuilder().port(remoteAddress.getPort());
            if (!remoteEndpoint.parseIp(remoteAddress.getAddress())) {
                remoteEndpoint.parseIp(remoteAddress.getHostName());
            }
            span.remoteEndpoint(remoteEndpoint.build());
        }
    }

//    static Result spanTracing(Span span, Tracer tracer, Context ctx, FilterChain chain){
//        boolean isOneway = false;
//        boolean deferFinish = false;
//        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
//            Result result = invoker.invoke(invocation);
//            if (result.hasException()) {
//                onError(result.getException(), span);
//            }
//            isOneway = RpcUtils.isOneway(invoker.getUrl(), invocation);
//            Future<Object> future = rpcContext.getFuture(); // the case on async client invocation
//            if (future instanceof FutureAdapter) {
//                deferFinish = true;
//                ((FutureAdapter) future).getFuture().setCallback(new FinishSpanCallback(span));
//            }
//            return result;
//        } catch (Exception e) {
//            onError(e, span);
//            throw e;
//        } finally {
//            if (isOneway) {
//                span.flush();
//            } else if (!deferFinish) {
//                span.finish();
//            }
//        }
//    }
//
//    static void onError(Throwable error, Span span) {
//        span.error(error);
//        if (error instanceof RpcException) {
//            span.tag("dubbo.error_code", Integer.toString(((RpcException) error).getCode()));
//        }
//    }
//
//    static final class FinishSpanCallback implements ResponseCallback {
//        final Span span;
//
//        FinishSpanCallback(Span span) {
//            this.span = span;
//        }
//
//        @Override public void done(Object response) {
//            span.finish();
//        }
//
//        @Override public void caught(Throwable exception) {
//            onError(exception, span);
//            span.finish();
//        }
//    }
}