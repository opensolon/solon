package org.noear.solon.cloud.model;

/**
 * @author noear
 * @since 1.3
 */
public enum MetricType {
    /**
     * 累积:该值是一个总数，从给定的开始时间开始累积。例如，自进程启动以来检测到的错误总数。
     * */
    cumulative,
    /**
     * 增量:在指定时间段内修改。例如，每分钟检测到的错误数。
     * */
    delta,
    /**
     * 量规:量值是一个连续变化的度量单位在某一特定时间的瞬时样本。例如，CPU的当前温度。
     * */
    gauge,
}
