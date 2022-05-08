package org.noear.solon.cloud.tracing.integration;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.event.EventListener;

/**
 * 异常监听适配
 *
 * @author noear
 * @since 1.4
 */
public class ErrorListenerAdapter implements EventListener<Throwable> {
    private Tracer tracer;

    public ErrorListenerAdapter() {
        Aop.getAsyn(Tracer.class, bw -> {
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
