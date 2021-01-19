package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.model.Event;

/**
 * 云端事件服务（事件总线服务）
 *
 * @author noear
 * @since 1.2
 */
public interface CloudEventService {
    /**
     * 发送事件
     */
    void send(Event event);

    /**
     * 关注事件（相当于订阅）
     */
    void attention(String queue, String topic, CloudEventHandler observer);
}
