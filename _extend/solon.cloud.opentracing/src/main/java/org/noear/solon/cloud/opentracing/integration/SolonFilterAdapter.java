package org.noear.solon.cloud.opentracing.integration;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.utils.LocalUtils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * @author noear
 * @since 1.4
 */
public class SolonFilterAdapter implements Filter {
    private Tracer tracer;

    public SolonFilterAdapter() {
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
            } catch (Throwable e) {
                span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                span.finish();
            }
        }
    }

    public Span buildSpan(Context ctx) {
        int port = ctx.uri().getPort();
        if (port < 1) {
            port = Solon.cfg().serverPort();
        }
        String ip = LocalUtils.getLocalAddress();

        //实例化构建器
        StringBuilder spanName = new StringBuilder();
        spanName.append(ctx.pathNew()).append(" (").append(ctx.uri().getScheme()).append(" ").append(ip).append(":").append(port).append(')');
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName.toString());

        //添加标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);

        //获取上下文
        SpanContext spanContext = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapAdapter(ctx.headerMap()));
        if (spanContext != null) {
            //如果有，说明是从上传传导过来的
            spanBuilder = spanBuilder.asChildOf(spanContext);
        }

        Span span = spanBuilder.start();

        //尝试注入
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(ctx.headerMap()));

        //开始
        return span;
    }
}
