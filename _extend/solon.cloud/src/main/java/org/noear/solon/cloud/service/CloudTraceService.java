package org.noear.solon.cloud.service;

/**
 * 云端链路跟踪服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudTraceService {
    /**
     * TraceId 头名称
     */
    String HEADER_TRACE_ID_NAME();

    /**
     * 获取跟踪标识
     */
    String getTraceId();

//    /**
//     * 记录节点关系
//     * */
//    void logLine(String from, String to);
//
//    /**
//     * 记录舜间值
//     * */
//    void logGauge(String group, String name, long val);
//
//    /**
//     * 记录累积值
//     * */
//    void logCount(String group, String name, long add);
//
//    /**
//     * 记录平均值
//     * */
//    void logMeter(String group, String name, long ref);

}
