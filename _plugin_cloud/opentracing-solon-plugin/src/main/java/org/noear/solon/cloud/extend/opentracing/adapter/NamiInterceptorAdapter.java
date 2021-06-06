package org.noear.solon.cloud.extend.opentracing.adapter;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import org.noear.nami.Interceptor;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.solon.core.Aop;

/**
 * @author noear
 * @since 1.4
 */
public class NamiInterceptorAdapter implements Interceptor {
    private Tracer tracer;

    public NamiInterceptorAdapter() {
        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }

    @Override
    public Result doIntercept(Invocation inv) throws Throwable {
        if (inv.method == null || tracer == null) {
            return inv.invoke();
        } else {
            Span span = buildSpan(inv);

            try (Scope scope = tracer.activateSpan(span)) {
                return inv.invoke();
            } finally {
                span.finish();
            }
        }
    }

    public Span buildSpan(Invocation inv) {
        //实例化构建器
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(inv.method.getName());

        //添加标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);

        //尝试注入
        tracer.inject(spanBuilder.start().context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(inv.headers));

        //开始
        return spanBuilder.start();
    }
}
