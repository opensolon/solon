/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.cloud.telemetry;


import io.opentelemetry.api.trace.Span;
import org.noear.solon.cloud.telemetry.integration.SpanSimulate;

import java.util.function.Consumer;

/**
 * 跟踪工具
 *
 * @author noear
 * @since 3.0
 */
public class Spans {
    /**
     * 活动中的Span（可能为Null；不推荐用）
     */
    public static Span active() {
        Span span = Span.current();
        if (span != null) {
            return span;
        } else {
            //避免出现 NullPointerException
            return SpanSimulate.getInstance();
        }
    }

    /**
     * 活动中的Span
     */
    public static void active(Consumer<Span> consumer) {
        Span span = Span.current();
        if (span != null) {
            consumer.accept(span);
        }
    }
}
