package org.noear.solon.cloud.extend.zipkin.service;

import brave.Tracing;
import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.tracing.service.TracerFactory;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author blackbear2003
 * @since 2.3
 */
public class ZipkinTracerFactory implements TracerFactory {
    final CloudProps cloudProps;

    public ZipkinTracerFactory(CloudProps cloudProps) {
        this.cloudProps = cloudProps;
    }

    @Override
    public Tracer create() throws Exception {

        Sender sender = OkHttpSender.create(cloudProps.getServer());

        Reporter<Span> spanReporter = AsyncReporter.create(sender);

        Tracing braveTracing = Tracing.newBuilder()
                .localServiceName(Solon.cfg().appName())
                .spanReporter(spanReporter)
                .build();

        return BraveTracer.create(braveTracing);
    }
}
