package org.noear.solon.cloud.tracing.annotation;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.util.InvKeys;

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
        if(tracer == null){
            return inv.invoke();
        }


        Tracing anno = inv.method().getAnnotation(Tracing.class);

        if (anno == null) {
            //支持注解在类上
            anno = inv.method().getMethod().getDeclaringClass().getAnnotation(Tracing.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            Span span = buildSpan(inv, anno);

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

    public Span buildSpan(Invocation inv, Tracing anno) {
        String spanName = Utils.annoAlias(anno.value(), anno.name());
        if (Utils.isEmpty(spanName)) {
            spanName = inv.method().getMethod().getDeclaringClass().getName()
                    + "::"
                    + inv.method().getMethod().getName();
        }

        //实例化构建器
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(spanName);

        //添加标志
        String tags = InvKeys.buildByTmlAndInv(anno.tags(), inv);
        if (Utils.isNotEmpty(tags)) {
            for (String tag : tags.split(",")) {
                String[] kv = tag.split("=");
                spanBuilder.withTag(kv[0], kv[1]);
            }
        }

        Span span = spanBuilder.start();


        //开始
        return span;
    }
}
