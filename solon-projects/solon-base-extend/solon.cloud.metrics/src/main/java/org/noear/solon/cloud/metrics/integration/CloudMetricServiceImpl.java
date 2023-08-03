package org.noear.solon.cloud.metrics.integration;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.noear.solon.cloud.service.CloudMetricService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.4
 */
public class CloudMetricServiceImpl implements CloudMetricService {
    @Override
    public void addCounter(String group, String category, String item, long increment, Map<String, String> attrs) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(":").append(category).append(":").append(item);

        Metrics.counter(buf.toString(), getTags(attrs)).increment(increment);
    }

    @Override
    public void addTimer(String group, String category, String item, long record, Map<String, String> attrs) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(":").append(category).append(":").append(item);

        Metrics.timer(buf.toString(), getTags(attrs)).record(record, TimeUnit.MICROSECONDS);
    }

    @Override
    public void addGauge(String group, String category, String item, long number, Map<String, String> attrs) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(":").append(category).append(":").append(item);

        Metrics.gauge(buf.toString(), getTags(attrs), number);
    }

    protected Iterable<Tag> getTags(Map<String, String> tags) {
        List<Tag> tagList = new ArrayList<>();

        if (tags != null) {
            for (Map.Entry<String, String> kv : tags.entrySet()) {
                tagList.add(Tag.of(kv.getKey(), kv.getValue()));
            }
        }

        return tagList;
    }
}
