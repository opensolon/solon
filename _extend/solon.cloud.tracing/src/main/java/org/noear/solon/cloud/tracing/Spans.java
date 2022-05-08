package org.noear.solon.cloud.tracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.solon.cloud.tracing.integration.SpanSimulate;
import org.noear.solon.core.Aop;

import java.util.function.Consumer;

/**
 * 跟踪工具
 *
 * @author noear
 * @since 1.7
 */
public class Spans {
    private static Tracer tracer;

    static {
        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }

    /**
     * 活动中的Span（可能为Null；不推荐用）
     */
    public static Span active() {
        if (tracer == null) {
            //避免出现 NullPointerException
            return SpanSimulate.getInstance();
        } else {
            return tracer.activeSpan();
        }
    }

    /**
     * 活动中的Span
     */
    public static void active(Consumer<Span> consumer) {
        if (tracer != null) {
            consumer.accept(tracer.activeSpan());
        }
    }
}
