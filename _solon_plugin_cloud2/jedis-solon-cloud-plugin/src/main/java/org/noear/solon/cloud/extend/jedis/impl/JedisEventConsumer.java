package org.noear.solon.cloud.extend.jedis.impl;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.jedis.JedisProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;

/**
 * @author noear
 * @since 1.10
 */
public class JedisEventConsumer extends JedisPubSub {
    static Logger log = LoggerFactory.getLogger(JedisEventConsumer.class);

    CloudEventObserverManger observerManger;
    String eventChannelName;

    public JedisEventConsumer(CloudProps cloudProps, CloudEventObserverManger observerManger) {
        this.observerManger = observerManger;
        this.eventChannelName = cloudProps.getEventChannel();
    }

    @Override
    public void onMessage(String channel, String message) {
        try {
            Event event = ONode.deserialize(message, Event.class);
            event.channel(eventChannelName);

            onReceive(event);
        }catch (Throwable ex){
            ex = Utils.throwableUnwrap(ex);

            EventBus.push(ex);

            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

    private boolean onReceive(Event event) {
        try {
            return onReceiveDo(event);
        } catch (Throwable e) {
            EventBus.push(e);
            return false;
        }
    }

    /**
     * 处理接收事件
     */
    private boolean onReceiveDo(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + JedisProps.GROUP_SPLIT_MART + event.topic();
        }

        handler = observerManger.get(topicNew);
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }
}
