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
     * 添加计数（累计）
     *
     * @param group     组
     * @param category  类别
     * @param item      项目
     * @param increment 增量值
     */
    void addCounter(String group, String category, String item, long increment);

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
    void addTimer(String group, String category, String item, long record);

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
    void addGauge(String group, String category, String item, long number);

    default void addGauge(String category, String item, long val) {
        addGauge(Solon.cfg().appName(), category, item, val);
    }
}
