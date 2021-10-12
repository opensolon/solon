package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.5
 */
public class RedisTopic {
    private final RedisClient redisX;
    private final String topicName;

    public RedisTopic(RedisClient redisX, String topicName) {
        this.redisX = redisX;
        this.topicName = topicName;
    }

    public void addListener(Consumer<String> consumer){

    }

    public void publish(String message){

    }
}
