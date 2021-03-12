package org.noear.solon.cloud.extend.rocketmq.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqConfig;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqConsumer;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceImp implements CloudEventService {

    RocketmqProducer producer;
    RocketmqConsumer consumer;

    public CloudEventServiceImp(String server) {
        RocketmqConfig config = new RocketmqConfig();
        config.server = server;

        producer = new RocketmqProducer(config);
        consumer = new RocketmqConsumer(config);
    }

    @Override
    public boolean publish(Event event) {
        return producer.publish(event);
    }


    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        topic = topic.replace(".", "_");

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        consumer.init(observerMap);
    }
}
