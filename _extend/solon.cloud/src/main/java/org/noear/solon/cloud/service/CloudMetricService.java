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
     * 添加度量（均值）
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param num 数值
     */
    void addMeter(String group, String category, String item, long num, boolean isolated);
    default void addMeter(String category, String item, long num, boolean isolated) {
        addMeter(Solon.cfg().appName(), category, item, num, isolated);
    }

    /**
     * 添加计量（舜值）
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

    /**
     * 添加记录（不能做日志）//或许不适合
     *
     * @param group 组
     * @param category 类别
     * @param item 项目
     * @param val 值
     */
    //void addLog(String group, String category, String item, Map<String, Object> val);
}
