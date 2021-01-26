package org.noear.solon.cloud.model;

import java.util.Date;
import java.util.Map;

/**
 * 度量
 *
 * @author noear
 * @since 1.3
 */
public class Metrics {
    public String key;
    public long value;
    public Map<String,String> tags;
    public Date timestamp;

    public MetricType metricType;
}
