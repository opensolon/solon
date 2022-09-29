package org.noear.solon.cloud.extend.jaeger.service;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.metrics.Metrics;
import io.jaegertracing.internal.metrics.NoopMetricsFactory;
import io.jaegertracing.internal.reporters.CompositeReporter;
import io.jaegertracing.internal.reporters.LoggingReporter;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Sender;
import io.jaegertracing.thrift.internal.senders.HttpSender;
import io.jaegertracing.thrift.internal.senders.UdpSender;
import io.opentracing.Tracer;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.tracing.service.TracerFactory;

import java.net.URI;

/**
 * @author noear
 * @since 1.7
 */
public class JaegerTracerFactory implements TracerFactory {
    final CloudProps cloudProps;

    public JaegerTracerFactory(CloudProps cloudProps) {
        this.cloudProps = cloudProps;
    }

    @Override
    public Tracer create() throws Exception {
        URI serverUri = URI.create(cloudProps.getServer());

        Sender sender;

        if ("http".equals(serverUri.getScheme())) {
            HttpSender.Builder builder = new HttpSender.Builder(cloudProps.getServer());

            if (Utils.isNotEmpty(cloudProps.getToken())) {
                builder.withAuth(cloudProps.getToken());
            }

            if (Utils.isNotEmpty(cloudProps.getUsername())) {
                builder.withAuth(cloudProps.getUsername(), cloudProps.getPassword());
            }

            sender = builder.build();
        } else {
            sender = new UdpSender(serverUri.getHost(), serverUri.getPort(), 0);
        }

        final CompositeReporter compositeReporter = new CompositeReporter(
                new RemoteReporter.Builder().withSender(sender).build(),
                new LoggingReporter()
        );

        final Metrics metrics = new Metrics(new NoopMetricsFactory());

        return new JaegerTracer.Builder(Solon.cfg().appName())
                .withReporter(compositeReporter)
                .withMetrics(metrics)
                .withExpandExceptionLogs()
                .withSampler(new ConstSampler(true)).build();
    }
}