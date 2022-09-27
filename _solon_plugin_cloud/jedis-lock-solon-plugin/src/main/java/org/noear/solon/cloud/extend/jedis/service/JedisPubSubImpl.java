package org.noear.solon.cloud.extend.jedis.service;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.model.Event;
import redis.clients.jedis.JedisPubSub;

/**
 * @author noear
 * @since 1.10
 */
public class JedisPubSubImpl extends JedisPubSub {
    String channel;
    String group;
    String topic;
    CloudEventHandler observer;

    public JedisPubSubImpl(String channel, String group, String topic, CloudEventHandler observer) {
        this.channel = channel;
        this.group = group;
        this.topic = topic;
        this.observer = observer;
    }

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals(topic)) {
            Event event = ONode.deserialize(message, Event.class);

            try {
                observer.handle(event);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
