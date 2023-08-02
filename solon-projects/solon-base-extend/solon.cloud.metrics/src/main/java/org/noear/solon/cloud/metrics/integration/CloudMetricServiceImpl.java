package org.noear.solon.cloud.metrics.integration;

import io.micrometer.core.instrument.Metrics;
import org.noear.solon.cloud.service.CloudMetricService;

import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.4
 */
public class CloudMetricServiceImpl implements CloudMetricService {
    @Override
    public void addCounter(String group, String category, String item, long increment) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(":").append(category).append(":").append(item);

        Metrics.counter(buf.toString()).increment(increment);
    }

    @Override
    public void addTimer(String group, String category, String item, long record) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(":").append(category).append(":").append(item);

        Metrics.timer(buf.toString()).record(record, TimeUnit.MICROSECONDS);
    }

    @Override
    public void addGauge(String group, String category, String item, long number) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(":").append(category).append(":").append(item);

        Metrics.gauge(buf.toString(), number);
    }
}
