package org.noear.solon.extend.cloud.service;

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
     * 订阅事件
     * */
    void subscribe(String topic);
}
