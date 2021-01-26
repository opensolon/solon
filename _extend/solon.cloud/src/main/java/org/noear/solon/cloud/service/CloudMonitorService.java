package org.noear.solon.cloud.service;

/**
 * 云端监控服务
 *
 * <p><code>
 * CloudMonitorService agent = new CloudMonitorService("yourAppName");
 *
 * agent.increment("myapp.login");
 * agent.addGauge("heap_free", 8675309);
 * agent.addTimeMetric("some.longProcess", 102);
 * agent.addMetric("Maintenance Now.", 600);
 * </code></p>
 *
 * @author noear
 * @since 1.2
 */
public interface CloudMonitorService {
    //Counter接口 //计数接口：计数器的值永远不会减少。
    void increment(String key);
    void increment(String key, long delta);

    //Gauges接口 // 仪表接口:衡量是一个离散值的值在任何给定的时刻,像“heap_used”或“current_temperature”。
    void addGauge(String key, double value);

    //Metrics接口 //指标接口：一个度量通过分配,跟踪,通常用于计时。通过跟踪计数、最小值、最大值、平均值(平均值)和简单的基于桶的分布直方图来收集指标。该分布可用于确定中位数、90百分位等。
    void addMetric(String key, long value);

    void addTimeMetric(String key, long timeInMillis);
}
