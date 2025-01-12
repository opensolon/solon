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
package org.noear.solon.cloud.tracing.integration;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import org.noear.nami.Context;
import org.noear.nami.Filter;
import org.noear.nami.Invocation;
import org.noear.nami.Result;
import org.noear.nami.common.TextUtils;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.tracing.slf4j.TracingMDC;

/**
 * Nami Tracing 过滤器适配
 *
 * @author noear
 * @since 1.4
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

            try (Scope scope = tracer.activateSpan(span)) {
                TracingMDC.inject(span);

                return inv.invoke();
            } catch (Throwable e) {
                span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                TracingMDC.removeSpanId();
                span.finish();
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
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName.toString());

        //添加种类标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);

        Span span = spanBuilder.start();

        //尝试注入
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(ctx.headers));

        //开始
        return span;
    }
}
