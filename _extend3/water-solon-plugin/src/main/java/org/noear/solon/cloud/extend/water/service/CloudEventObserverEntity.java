package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;
import org.noear.water.model.MessageM;
import org.noear.water.utils.ext.Fun1Ex;

/**
 * @author noear 2021/1/22 created
 */
public class CloudEventObserverEntity implements Fun1Ex<MessageM,Boolean> {
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
    public Boolean run(MessageM msg) throws Throwable {
        Event event = new Event();
        event.key = msg.key;
        event.topic = msg.topic;
        event.content = msg.message;
        event.tags = msg.tags;

        return handler.handler(event);
    }
}
