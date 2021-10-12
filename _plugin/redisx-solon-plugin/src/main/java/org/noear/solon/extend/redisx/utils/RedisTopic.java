package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisX;

import java.util.function.Consumer;

/**
 * @author noear
 * @since 1.5
 */
public class RedisTopic {
    private final RedisX redisX;
    private final String topicName;

    public RedisTopic(RedisX redisX, String topicName) {
        this.redisX = redisX;
        this.topicName = topicName;
    }

    public void addListener(Consumer<String> consumer){

    }

    public void publish(String message){

    }
}
