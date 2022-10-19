package org.noear.solon.cloud.model;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.annotation.EventLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 云端事件主题观察者（可以添加多个处理，用于二级分发）
 *
 * @author noear
 * @since 1.2
 */
public class EventObserver implements CloudEventHandler {
    private static Logger log = LoggerFactory.getLogger(EventObserver.class);

    private final EventLevel level;
    private final String group;
    private final String topic;
    private final List<CloudEventHandler> handlers;


    public EventObserver(EventLevel level, String group, String topic) {
        this.level = level;
        this.group = group;
        this.topic = topic;
        this.handlers = new ArrayList<>();
    }

    public EventLevel getLevel() {
        return level;
    }

    public String getGroup() {
        return group;
    }

    public String getTopic() {
        return topic;
    }

    /**
     * 添加云事件处理
     */
    public void addHandler(CloudEventHandler handler) {
        handlers.add(handler);
    }

    /**
     * 处理
     */
    @Override
    public boolean handle(Event event) throws Throwable {
        boolean isOk = true;
        boolean isHandled = false;

        for (CloudEventHandler h1 : handlers) {
            isOk = isOk && handlerDo(event, h1); //两个都成功，才是成功
            isHandled = true;
        }

        if (isHandled == false) {
            //只需要记录一下
            log.warn("There is no handler for this event topic[{}]", event.topic());
        }

        return isOk;
    }

    private boolean handlerDo(Event event, CloudEventHandler handler) throws Throwable {
        if (CloudManager.eventInterceptor() == null) {
            return handler.handle(event);
        } else {
            return CloudManager.eventInterceptor().doIntercept(event, handler);
        }
    }
}
