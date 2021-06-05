package org.noear.solon.cloud.extend.zipkin.filter;


import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContextOrSamplingFlags;

import org.noear.snack.ONode;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

import java.util.Map;



/**
 * @author noear 2021/6/4 created
 */
public class SolonFilter implements Filter {

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

    private Tracer tracer;

    // tracing上下文消息提取
    private TraceContext.Extractor<Map<String, String>> extractor;

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        long time = System.currentTimeMillis();
        Tracing tracing = Aop.get(Tracing.class);
        tracer = tracing.tracer();
        if (null == tracer) {
            chain.doFilter(ctx);
            return;
        }

        if (null == extractor) {
            extractor = tracing.propagation().extractor(GETTER);
        }

        TraceContextOrSamplingFlags extracted = extractor.extract(ctx.headerMap());
        Span span = extracted.context() != null
                ? tracer.joinSpan(extracted.context())
                : tracer.nextSpan(extracted);

        //ZipkinHelper.buildSpan(span, Span.Kind.SERVER, ctx.realIp(),ctx.realIp(), ctx.path());
        //ZipkinHelper.spanTracing(span, tracer, invoker, invocation, rpcContext);

    }
}
