package org.noear.solon.cloud.opentracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.solon.core.Aop;

import java.util.function.Consumer;

/**
 * 跟踪工具
 *
 * @author noear
 * @since 1.7
 */
public class TracingUtil {
    private static Tracer tracer;
    static {
        Aop.getAsyn(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }

    /**
     * 活动中的Span（可能为Null；不推荐用）
     * */
    public static Span activeSpan() {
        if (tracer == null) {
            return null;
        } else {
            return tracer.activeSpan();
        }
    }

    /**
     * 活动中的Span
     * */
    public static void activeSpan(Consumer<Span> consumer) {
        Span span = activeSpan();

        if (span != null) {
            consumer.accept(span);
        }
    }
}
