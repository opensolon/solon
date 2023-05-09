package org.noear.solon.cloud.tracing.slf4j;

import io.opentracing.Span;
import org.slf4j.MDC;

/**
 * 跟踪 MDC（将跟踪标识注入到 MDC）
 *
 * @author orangej
 * @since 2.2
 */
public class TracingMDC {
    public static final String TRACE_ID_NAME = "X-TraceId";
    public static final String SPAN_ID_NAME = "X-SpanId";

    /**
     * 注入 traceId 和 spanId
     * */
    public static void inject(Span span) {
        MDC.put(TRACE_ID_NAME, span.context().toTraceId());
        MDC.put(SPAN_ID_NAME, span.context().toSpanId());
    }

    /**
     * 移除 traceId
     * */
    public static void removeTraceId() {
        MDC.remove(TRACE_ID_NAME);
    }

    /**
     * 移除 spanId
     * */
    public static void removeSpanId() {
        MDC.remove(SPAN_ID_NAME);
    }
}
