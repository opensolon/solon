package org.noear.solon.cloud.extend.rabbitmq.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitChannelFactory;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitConfig;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitConsumer;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceRabbitmqImp implements CloudEventService {
    private static CloudEventServiceRabbitmqImp instance;
    public static synchronized CloudEventServiceRabbitmqImp getInstance() {
        if (instance == null) {
            instance = new CloudEventServiceRabbitmqImp();
        }

        return instance;
    }


    RabbitProducer producer;
    RabbitConsumer consumer;

    private CloudEventServiceRabbitmqImp() {

        try {
            RabbitConfig config = new RabbitConfig();
            config.server = RabbitmqProps.instance.getEventServer();
            config.username = RabbitmqProps.instance.getUsername();
            config.password = RabbitmqProps.instance.getPassword();

            RabbitChannelFactory factory = new RabbitChannelFactory(config);

            producer = new RabbitProducer(factory);
            consumer = new RabbitConsumer(producer, factory);

            producer.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean publish(Event event) {
        try {
            if(Utils.isEmpty(event.key())){
                event.key(Utils.guid());
            }

            return producer.publish(event);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        try {
            consumer.init(observerMap);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
