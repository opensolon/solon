package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

/**
 * 云端度量服务
 *
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
     * @param val 数值
     */
    void addCount(String group, String category, String item, long val);
    default void addCount(String category, String item, long val) {
        addCount(Solon.cfg().appName(), category, item, val);
    }

    /**
     * 添加度量（平均值、最大值、最小值）
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param val 数值
     */
    void addMeter(String group, String category, String item, long val);
    default void addMeter(String category, String item, long val) {
        addMeter(Solon.cfg().appName(), category, item, val);
    }

    /**
     * 添加指标（瞬值）
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param val 数值
     */
    void addGauge(String group, String category, String item, long val);
    default void addGauge(String category, String item, long val) {
        addGauge(Solon.cfg().appName(), category, item, val);
    }
}
