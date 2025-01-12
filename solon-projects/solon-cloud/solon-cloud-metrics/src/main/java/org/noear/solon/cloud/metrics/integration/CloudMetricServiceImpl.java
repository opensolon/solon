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
        buf.append(group).append(".").append(category).append(".").append(item);

        Metrics.counter(buf.toString(), getTags(attrs)).increment(increment);
    }

    @Override
    public void addTimer(String group, String category, String item, long record, Map<String, String> attrs) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(".").append(category).append(".").append(item);

        Metrics.timer(buf.toString(), getTags(attrs)).record(record, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addGauge(String group, String category, String item, long number, Map<String, String> attrs) {
        StringBuilder buf = new StringBuilder();
        buf.append(group).append(".").append(category).append(".").append(item);

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
