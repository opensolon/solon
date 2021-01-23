package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;

/**
 * 云端事件服务（事件总线服务）
 *
 * @author noear
 * @since 1.2
 */
public interface CloudEventService {
    /**
     * 推送事件
     */
    boolean push(Event event);

    /**
     * 关注事件（相当于订阅）
     */
    void attention(EventLevel level, String queue, String topic, CloudEventHandler observer);
}
