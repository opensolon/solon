package org.noear.solon.opentracing.demo;

import io.jaegertracing.internal.JaegerTracer;
import io.jaegertracing.internal.metrics.Metrics;
import io.jaegertracing.internal.metrics.NoopMetricsFactory;
import io.jaegertracing.internal.reporters.CompositeReporter;
import io.jaegertracing.internal.reporters.InMemoryReporter;
import io.jaegertracing.internal.reporters.LoggingReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2021/6/6 created
 */
@Configuration
public class Config {
    @Inject("${io.opentracing.trace.host}")
    private String AGENT_HOST;

    @Inject("${io.opentracing.trace.port}")
    private Integer AGENT_PORT;

    @Bean
    public Tracer tracer() {
        final CompositeReporter compositeReporter = new CompositeReporter(
                new InMemoryReporter(),
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
