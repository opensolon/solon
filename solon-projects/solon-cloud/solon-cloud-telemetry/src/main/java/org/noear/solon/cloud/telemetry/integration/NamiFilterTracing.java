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
package org.noear.solon.cloud.telemetry.integration;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Scope;
import org.noear.nami.Context;
import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.nami.common.TextUtils;
import org.noear.solon.Solon;
import org.noear.solon.cloud.telemetry.slf4j.TracingMDC;

/**
 * Nami Tracing 过滤器适配
 *
 * @author noear
 * @since 3.0
 */
public class NamiFilterTracing implements Filter {
    private Tracer tracer;

    public NamiFilterTracing() {
        Solon.context().getBeanAsync(Tracer.class, bean -> {
            tracer = bean;
        });
    }

    @Override
    public Result doFilter(Invocation inv) throws Throwable {
        if (tracer == null) {
            return inv.invoke();
        } else {
            Span span = buildSpan(inv);

            try (Scope scope = span.makeCurrent()){
                TracingMDC.inject(span);

                return inv.invoke();
            } catch (Throwable e) {
                //span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                TracingMDC.removeSpanId();
                span.end();
            }
        }
    }

    public Span buildSpan(Context ctx) {
        //构建 Span Name
        StringBuilder operationName = new StringBuilder();

        operationName.append("Nami:");
        if (TextUtils.isEmpty(ctx.config.getName())) {
            operationName.append(ctx.url);
        } else {
            operationName.append(ctx.config.getName()).append(":");
            operationName.append(ctx.url);
        }


        //实例化构建器
        SpanBuilder spanBuilder = tracer.spanBuilder(operationName.toString());

        //添加种类标志
        spanBuilder.setSpanKind(SpanKind.CLIENT);


        Span span = spanBuilder.startSpan();

        //尝试注入
        //tracer.inject(span.context(), HTTP_HEADERS, new TextMapAdapter(ctx.headers));

        //开始
        return span;
    }
}
