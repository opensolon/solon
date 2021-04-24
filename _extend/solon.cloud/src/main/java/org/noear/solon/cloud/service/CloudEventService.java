package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;

/**
 * 云端事件服务（事件总线服务）
 *
 * @author noear
 * @since 1.2
 */
public interface CloudEventService {
    /**
     * 发布事件
     */
    boolean publish(Event event) throws CloudEventException;

    /**
     * 关注事件（相当于订阅）
     */
    void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer);
}
