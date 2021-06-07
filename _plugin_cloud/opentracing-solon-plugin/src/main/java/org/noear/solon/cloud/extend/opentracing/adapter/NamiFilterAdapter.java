package org.noear.solon.cloud.extend.opentracing.adapter;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import org.noear.nami.NamiContext;
import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.nami.common.TextUtils;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class NamiFilterAdapter implements Filter {
    private Tracer tracer;

    public NamiFilterAdapter() {
        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }

    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        if (tracer == null) {
            return inv.invoke();
        } else {
            Span span = buildSpan(inv);

            try (Scope scope = tracer.activateSpan(span)) {
                Map<String, Object> logMap = new LinkedHashMap<>();
                logMap.put("args", inv.args);
                span.log(logMap);

                return inv.invoke();
            } catch (Throwable e) {
                span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                span.finish();
            }
        }
    }

    public Span buildSpan(NamiContext ctx) {
        //构建 Span Name
        StringBuilder spanName = new StringBuilder();

        spanName.append("Nami::");
        if (TextUtils.isEmpty(ctx.config.getName())) {
            spanName.append(ctx.url);
        } else {
            spanName.append(ctx.config.getName()).append("::");
            spanName.append(ctx.url);
        }


        //实例化构建器
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName.toString());

        //添加标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);

        Span span = spanBuilder.start();

        //尝试注入
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(ctx.headers));

        //开始
        return span;
    }
}
