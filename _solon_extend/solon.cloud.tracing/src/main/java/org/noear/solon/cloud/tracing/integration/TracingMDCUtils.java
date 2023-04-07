package org.noear.solon.cloud.tracing.integration;

import io.opentracing.Span;
import org.slf4j.MDC;

/**
 * 在日志中注入跟踪信息
 *
 * @author orangej
 * @since 2023/4/7
 */
public class TracingMDCUtils {

    private static final String TRACE_ID_NAME = "X-TraceId";
    private static final String SPAN_ID_NAME = "X-SpanId";

    public static void inject(Span span) {
        MDC.put(TRACE_ID_NAME, span.context().toTraceId());
        MDC.put(SPAN_ID_NAME, span.context().toSpanId());
    }

    public static void clear() {
        MDC.remove(TRACE_ID_NAME);
        MDC.remove(SPAN_ID_NAME);
    }

}
