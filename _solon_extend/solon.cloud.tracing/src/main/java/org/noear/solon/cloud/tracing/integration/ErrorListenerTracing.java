package org.noear.solon.cloud.tracing.integration;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventListener;

/**
 * 异常监听适配
 *
 * @author noear
 * @since 1.4
 */
public class ErrorListenerTracing implements EventListener<Throwable> {
    private Tracer tracer;

    public ErrorListenerTracing() {
        Solon.context().getWrapAsync(Tracer.class, bw -> {
            tracer = bw.raw();
        });
    }

    @Override
    public void onEvent(Throwable e) {
        if (tracer == null) {
            return;
        }

        Span span = tracer.activeSpan();

        if (span != null) {
            span.log(Utils.throwableToString(e));
        }
    }
}
