package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

import java.util.Map;

/**
 * 云端度量服务
 *
 * @author noear
 * @since 1.4
 */
public interface CloudMetricService {
    /**
     * 添加计数（累计）
     *
     * @param group     组
     * @param category  类别
     * @param item      项目
     * @param increment 增量值
     */
    void addCounter(String group, String category, String item, long increment, Map<String, String> attrs);

    default void addCounter(String group, String category, String item, long increment) {
        addCounter(group, category, item, increment, null);
    }

    default void addCounter(String category, String item, long increment) {
        addCounter(Solon.cfg().appName(), category, item, increment);
    }

    /**
     * 添加计时（平均值、最大值、最小值）
     *
     * @param group    组
     * @param category 类别
     * @param item     项目
     * @param record   记录值
     */
    void addTimer(String group, String category, String item, long record, Map<String, String> attrs);

    default void addTimer(String group, String category, String item, long record) {
        addTimer(group, category, item, record, null);
    }

    default void addTimer(String category, String item, long record) {
        addTimer(Solon.cfg().appName(), category, item, record);
    }


    /**
     * 添加指标（瞬值）
     *
     * @param group    组
     * @param category 类别
     * @param item     项目
     * @param number   瞬间值
     */
    void addGauge(String group, String category, String item, long number, Map<String, String> attrs);

    default void addGauge(String group, String category, String item, long number) {
        addGauge(group, category, item, number, null);
    }

    default void addGauge(String category, String item, long val) {
        addGauge(Solon.cfg().appName(), category, item, val);
    }
}
