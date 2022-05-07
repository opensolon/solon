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
    public static Span activeSpan() {
        Tracer tracer = Aop.get(Tracer.class);

        if (tracer == null) {
            return null;
        } else {
            return tracer.activeSpan();
        }
    }

    public static void activeSpan(Consumer<Span> consumer) {
        Span span = activeSpan();

        if (span != null) {
            consumer.accept(span);
        }
    }
}
