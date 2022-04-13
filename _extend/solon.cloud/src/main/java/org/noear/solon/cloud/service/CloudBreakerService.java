package org.noear.solon.cloud.service;

import org.noear.solon.cloud.model.BreakerException;

/**
 * 云端断路器服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudBreakerService {
    /**
     * 获取入口
     *
     * @param breakerName 断路器名称
     */
    AutoCloseable entry(String breakerName) throws BreakerException;

    /**
     * 是否存在入口
     * */
    boolean exists(String breakerName);
}
