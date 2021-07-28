package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.cloud.service.CloudEventServicePlus;


/**
 * 事件服务管理器及代理（用以支持多通道）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudEventManager extends CloudEventService {

    /**
     * 注册事件服务
     *
     * @param service 事件服务
     */
    void register(CloudEventServicePlus service);

    /**
     * 获取事件服务
     *
     * @param channel 通道
     */
    CloudEventServicePlus get(String channel);

    /**
     * 获取事件服务，如果没有则异常
     *
     * @param channel 通道
     */
    CloudEventServicePlus getOrThrow(String channel);
}
