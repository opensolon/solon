package org.noear.solon.cloud.extend.zipkin.filter;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * @author noear
 * @since 1.4
 */
public class FilterAdapter implements Filter {
    private Tracer tracer;

    public FilterAdapter() {
        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (tracer == null) {
            chain.doFilter(ctx);
        } else {
            Span span = buildSpan(ctx);

            try (Scope scope = tracer.activateSpan(span)) {
                chain.doFilter(ctx);
            } finally {
                span.finish();
            }
        }
    }

    public Span buildSpan(Context ctx) {
        //实例化构建器
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(ctx.path());

        //添加标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);

        //获取提取器
        SpanContext extract = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(ctx.headerMap()));
        if (extract != null) {
            //如果有，说明是从上传传导过来的
            spanBuilder = spanBuilder.asChildOf(extract);
        }

        //尝试注入
        tracer.inject(spanBuilder.start().context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(ctx.headerMap()));

        //开始
        return spanBuilder.start();
    }
}
