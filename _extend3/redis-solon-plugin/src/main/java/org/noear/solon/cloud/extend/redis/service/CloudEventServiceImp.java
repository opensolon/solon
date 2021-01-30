package org.noear.solon.cloud.extend.redis.service;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.redis.impl.RedisConsumer;
import org.noear.solon.cloud.extend.redis.impl.RedisX;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
@Deprecated
public class CloudEventServiceImp implements CloudEventService {

    RedisX redisX;

    public CloudEventServiceImp(RedisX redisX) {
        this.redisX = redisX;
    }

    @Override
    public boolean publish(Event event) {
        String event_json = ONode.stringify(event);

        return redisX.open1(us -> us.publish(event.topic(), event_json)) > 0;
    }


    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();
    RedisConsumer redisConsumer = new RedisConsumer();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        try {
            redisConsumer.init(redisX, observerMap);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
