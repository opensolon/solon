package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * 云端事件观察者实体
 *
 * @author noear
 * @since 1.2
 */
public class CloudEventObserverListEntity implements CloudEventHandler {
    public EventLevel level;
    public String group;
    public String topic;
    public List<CloudEventHandler> handlers;

    public CloudEventObserverListEntity(EventLevel level, String group, String topic) {
        this.level = level;
        this.group = group;
        this.topic = topic;
        this.handlers = new ArrayList<>();
    }

    public void addHandler(CloudEventHandler handler) {
        handlers.add(handler);
    }

    @Override
    public boolean handler(Event event) throws Throwable {
        boolean isOk = true;

        for (CloudEventHandler h1 : handlers) {
            isOk = isOk && h1.handler(event);
        }

        return isOk;
    }
}
