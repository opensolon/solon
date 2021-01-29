package org.noear.solon.cloud.extend.redis.impl;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.utils.ExpirationUtils;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

/**
 * @author noear 2021/1/29 created
 */
public class RedisConsumer extends JedisPubSub {
    RedisX redisX;
    Map<String, CloudEventObserverEntity> observerMap;
    String queue_retry;

    public RedisConsumer() {
        queue_retry = Solon.cfg().appGroup() + "_" + Solon.cfg().appName() + "@retry";
    }

    public void init(RedisX redisX, Map<String, CloudEventObserverEntity> observerMap) {
        this.redisX = redisX;
        this.observerMap = observerMap;

        String[] topics = observerMap.keySet().toArray(new String[0]);

        new Thread(() -> {
            while (true) {
                try {
                    redisX.open0(us -> {
                        us.subscribe(this, topics);
                    });
                } catch (Throwable ex) {
                    try {
                        Thread.sleep(100);
                    } catch (Throwable e2) {

                    }
                }
            }
        }).start();
    }

    @Override
    public void onMessage(String channel, String message) {
        Event event = ONode.deserialize(message);

        CloudEventObserverEntity entity = observerMap.get(event.topic());
        if (entity != null) {
            boolean isHandled = false;
            try {
                isHandled = entity.handler(event);
            } catch (Throwable ex) {
                isHandled = false;
            }

            if (isHandled == false) {
                event.times(event.times() + 1);
                String event_json = ONode.stringify(event);
                long event_next = System.currentTimeMillis() + ExpirationUtils.getExpiration(event.times());

                redisX.open0(us -> {
                    us.key(queue_retry).zsetAdd(event_next, event_json);
                });
            }
        }
    }
}
