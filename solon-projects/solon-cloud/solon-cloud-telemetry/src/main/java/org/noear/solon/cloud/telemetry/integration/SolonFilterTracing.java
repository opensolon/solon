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
package org.noear.solon.cloud.telemetry.integration;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Scope;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.telemetry.slf4j.TracingMDC;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

import java.util.HashSet;
import java.util.Set;

/**
 * Solon Tracing 过滤器适配
 *
 * @author noear
 * @since 3.0
 */
public class SolonFilterTracing implements Filter {
    private Tracer tracer;
    private Set<String> excludePaths = new HashSet<>();

    public SolonFilterTracing(String excluded) {
        //跟踪器注入
        Solon.context().getBeanAsync(Tracer.class, bean -> {
            tracer = bean;
        });

        //排除支持
        if (Utils.isNotEmpty(excluded)) {
            for (String path : excluded.split(",")) {
                path = path.trim();

                if (path.length() > 0) {
                    if (path.startsWith("/")) {
                        excludePaths.add(path);
                    } else {
                        excludePaths.add("/" + path);
                    }
                }
            }
        }
    }

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (tracer == null || excludePaths.contains(ctx.pathNew())) {
            //没有跟踪器，或者排除
            chain.doFilter(ctx);
        } else {
            Span span = buildSpan(ctx);

            try (Scope scope = span.makeCurrent()){
                TracingMDC.inject(span);

                chain.doFilter(ctx);
            } catch (Throwable e) {
                //span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                TracingMDC.removeSpanId();
                TracingMDC.removeTraceId();
                span.end();
            }
        }
    }

    public Span buildSpan(Context ctx) {
        //实例化构建器
        StringBuilder operationName = new StringBuilder();
        operationName.append(ctx.pathNew()).append(" (").append(ctx.uri().getScheme()).append(" ").append(ctx.method()).append(')');
        SpanBuilder spanBuilder = tracer.spanBuilder(operationName.toString());

        //添加种类标志
        spanBuilder.setSpanKind(SpanKind.SERVER);
        spanBuilder.setAttribute("req.url", ctx.url());

        //获取上下文
//        TextMapAdapter headerMapAdapter = new TextMapAdapter(ctx.headerMap().toValueMap());
//
//        SpanContext spanContext = tracer.extract(Format.Builtin.HTTP_HEADERS, headerMapAdapter);
//        if (spanContext != null) {
//            //如果有，说明是从上传传导过来的
//            spanBuilder = spanBuilder.setParent(spanContext);
//        }

        Span span = spanBuilder.startSpan();

        //尝试注入
//        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, headerMapAdapter);

        //开始
        return span;
    }
}
