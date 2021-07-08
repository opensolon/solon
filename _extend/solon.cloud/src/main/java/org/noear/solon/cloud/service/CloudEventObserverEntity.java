package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;

/**
 * 云端事件观察者实体
 *
 * @author noear
 * @since 1.2
 */
public class CloudEventObserverEntity implements CloudEventHandler {
    public EventLevel level;
    public String queue;
    public String topic;
    public CloudEventHandler handler;

    public CloudEventObserverEntity(EventLevel level, String queue, String topic, CloudEventHandler handler) {
        this.level = level;
        this.queue = queue;
        this.topic = topic;
        this.handler = handler;
    }

    @Override
    public boolean handler(Event event) throws Throwable {
        return handler.handler(event);
    }
}
