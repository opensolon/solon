package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

/**
 * @author noear
 * @since 1.4
 */
public interface CloudMetricService {
    /**
     * 添加记数（累计）
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param num 数值
     */
    void addCount(String group, String category, String item, long num);
    default void addCount(String category, String item, long num) {
        addCount(Solon.cfg().appName(), category, item, num);
    }

    /**
     * 添加度量（平均值、最大值、最小值）
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param num 数值
     */
    void addMeter(String group, String category, String item, long num);
    default void addMeter(String category, String item, long num) {
        addMeter(Solon.cfg().appName(), category, item, num);
    }

    /**
     * 添加指标（瞬值）
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param val 值
     */
    void addGauge(String group, String category, String item, Object val);
    default void addGauge(String category, String item, Object val) {
        addGauge(Solon.cfg().appName(), category, item, val);
    }
}
