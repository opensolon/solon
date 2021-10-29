package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 云端事件观察者实体
 *
 * @author noear
 * @since 1.2
 */
public class CloudEventObserverEntity implements CloudEventHandler {
    static Logger log = LoggerFactory.getLogger(CloudEventObserverEntity.class);

    private EventLevel level;
    private String group;
    private String topic;
    private List<CloudEventHandler> handlers;


    public CloudEventObserverEntity(EventLevel level, String group, String topic) {
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
        boolean isHandled = false;

        for (CloudEventHandler h1 : handlers) {
            isOk = isOk && h1.handler(event); //两个都成功，才是成功
            isHandled = true;
        }

        if (isHandled == false) {
            //只需要记录一下
            log.warn("There is no handler for this event topic[{}]", event.topic());
        }

        return isOk;
    }
}
