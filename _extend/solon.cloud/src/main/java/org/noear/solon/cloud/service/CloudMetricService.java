package org.noear.solon.cloud.service;

import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public interface CloudMetricService {
    /**
     * 添加记数（累计）
     * */
    void addCount(String group, String name, int count);
    /**
     * 添加度量（均值）
     * */
    void addMeter(String group, String name, long interval, boolean isolated);

    /**
     * 添加计量（舜值）
     * */
    void addGauge(String group, String name, Object value);

    /**
     * 添加记录
     * */
    void addLog(String group, String name, Map<String,Object> data);
}
