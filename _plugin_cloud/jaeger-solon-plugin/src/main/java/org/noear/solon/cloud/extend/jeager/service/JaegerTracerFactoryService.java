package org.noear.solon.cloud.extend.jeager.service;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.metrics.Metrics;
import io.jaegertracing.internal.metrics.NoopMetricsFactory;
import io.jaegertracing.internal.reporters.CompositeReporter;
import io.jaegertracing.internal.reporters.LoggingReporter;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.thrift.internal.senders.UdpSender;
import io.opentracing.Tracer;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.opentracing.service.TracerFactoryService;

/**
 * @author noear
 * @since 1.7
 */
public class JaegerTracerFactoryService implements TracerFactoryService {
    final CloudProps cloudProps;

    public JaegerTracerFactoryService(CloudProps cloudProps) {
        this.cloudProps = cloudProps;
    }

    @Override
    public Tracer create() throws Exception {
        String[] serverPort = cloudProps.getServer().split(":");

        String server = serverPort[0];
        int port = Integer.parseInt(serverPort[1]);

        final CompositeReporter compositeReporter = new CompositeReporter(
                new RemoteReporter.Builder().withSender(new UdpSender(server, port, 0)).withFlushInterval(10).build(),
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