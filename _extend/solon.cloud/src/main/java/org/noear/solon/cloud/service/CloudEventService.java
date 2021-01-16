package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;

/**
 * 事件服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudEventService {
    /**
     * 发送事件
     * */
    void send(String topic, String content);

    /**
     * 发送事件
     * */
    void send(String id, String topic, String content);

    /**
     * 关注事件
     * */
    void attention(String topic, CloudEventHandler observer);
}
