package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;
import redis.clients.jedis.JedisPubSub;

import java.util.function.BiConsumer;

/**
 * Redis 总线
 *
 * @author noear
 * @since 1.5
 */
public class RedisBus {
    private final RedisClient client;

    public RedisBus(RedisClient client) {
        this.client = client;
    }

    public void addListener(BiConsumer<String, String> consumer, String... topics) {
        client.open0(session -> {
            session.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    consumer.accept(channel, message);
                }
            }, topics);
        });
    }

    public void publish(String topic, String message) {
        client.open0(session -> {
            session.publish(topic, message);
        });
    }
}
