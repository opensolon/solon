/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
