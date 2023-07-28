package org.noear.solon.cloud.metrics;

/**
 * 通用注册表
 *
 * @author bai
 * @since 2.4
 */
@FunctionalInterface
public interface CommonMeterRegistry<T> {


    /**
     * 注册表
     *
     * @param meterRegistry 计注册表
     */
    void registry(T meterRegistry);

}
