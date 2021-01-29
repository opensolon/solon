package org.noear.solon.cloud.extend.redis.service;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
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
public class CloudEventServiceImp implements CloudEventService {
    static final String channel_main = "channel.main";

    RedisX redisX;
    public CloudEventServiceImp(RedisX redisX){
        this.redisX = redisX;
    }

    @Override
    public boolean publish(Event event) {
        String event_json = ONode.stringify(event);

        return redisX.open1(us -> us.publish(channel_main, event_json)) > 0;
    }


    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }
}
