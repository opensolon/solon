package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;
import redis.clients.jedis.JedisPubSub;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Redis 主题
 *
 * @author noear
 * @since 1.5
 */
public class RedisTopic {
    private final RedisClient client;
    private final String topicName;

    public RedisTopic(RedisClient client, String topicName) {
        this.client = client;
        this.topicName = topicName;
    }

    public void addListener(Consumer<String> consumer) {
        client.open0(session -> {
            session.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    consumer.accept(message);
                }
            }, topicName);
        });
    }

    public void publish(String message) {
        client.open0(session -> {
            session.publish(topicName, message);
        });
    }

    public void publishAll(Collection<String> messageColl) {
        client.open0(session -> {
            for (String message : messageColl) {
                session.publish(topicName, message);
            }
        });
    }
}
