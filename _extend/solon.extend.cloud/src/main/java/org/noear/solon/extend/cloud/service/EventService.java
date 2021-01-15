package org.noear.solon.extend.cloud.service;

/**
 * 事件服务
 *
 * @author noear
 * @since 1.2
 */
public interface EventService {
    void push(String topic, String content);

    void push(String id, String topic, String content);

    void subscribe(String topic);
}
