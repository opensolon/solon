package org.noear.solon.extend.dubbo3.integration;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.tag.Tags;
import org.apache.dubbo.rpc.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;

/**
 * Dubbo Tracing 过滤器适配
 *
 * @author noear
 * @since 2.2
 */
public class DubboFilterTracing implements Filter {
    private Tracer tracer;

    public DubboFilterTracing() {
        Solon.context().getBeanAsync(Tracer.class, bean -> {
            tracer = bean;
        });
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (tracer == null) {
            return invoker.invoke(invocation);
        } else {
            Span span = buildSpan(invoker, invocation);

            try (Scope scope = tracer.activateSpan(span)) {
                return invoker.invoke(invocation);
            } catch (Throwable e) {
                span.log(Utils.throwableToString(e));
                throw e;
            } finally {
                span.finish();
            }
        }
    }

    public Span buildSpan(Invoker<?> invoker, Invocation invocation) {
        //构建 Span Name
        StringBuilder operationName = new StringBuilder();

        operationName.append("Dubbo:");
        if (Utils.isNotEmpty(invocation.getServiceName())) {
            operationName.append(invocation.getServiceName()).append(":");
        }
        operationName.append(invoker.getInterface().getName()).append(":");
        operationName.append(invocation.getMethodName());


        //实例化构建器
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName.toString());

        //添加种类标志
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);

        Span span = spanBuilder.start();

        //尝试注入
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, new TextMapAdapter(invocation.getAttachments()));

        //开始
        return span;
    }
}
