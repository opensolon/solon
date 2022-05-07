package org.noear.solon.cloud.extend.opentracing.around;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.opentracing.annotation.Tracing;
import org.noear.solon.core.Aop;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 1.7
 */
public class TracingInterceptor implements Interceptor {
    private Tracer tracer;

    public TracingInterceptor() {
        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }


    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Tracing anno = inv.method().getAnnotation(Tracing.class);

        if (anno == null) {
            return inv.invoke();
        } else {
            Span parentSpan = tracer.activeSpan();

            Span span = buildSpan(anno, parentSpan);


            try (Scope scope = tracer.activateSpan(span)) {
                return inv.invoke();
            } catch (Throwable e) {
                span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                span.finish();
            }
        }
    }

    public Span buildSpan(Tracing anno, Span parentSpan) {
        String spanName = Utils.annoAlias(anno.value(), anno.name());

        //实例化构建器
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName);

        //添加标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);

        String[] spanTags = anno.tags();
        if (spanTags != null) {
            for (String tag : spanTags) {
                String[] kv = tag.split("=");
                spanBuilder.withTag(kv[0], kv[1]);
            }
        }

        //添加父级
        if (parentSpan != null) {
            spanBuilder.asChildOf(parentSpan);
        }

        Span span = spanBuilder.start();

        //尝试注入


        //开始
        return span;
    }
}
