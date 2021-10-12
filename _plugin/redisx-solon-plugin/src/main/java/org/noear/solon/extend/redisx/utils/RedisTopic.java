package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

import java.util.function.Consumer;

/**
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

    public void addListener(Consumer<String> consumer){

    }

    public void publish(String message){

    }
}
