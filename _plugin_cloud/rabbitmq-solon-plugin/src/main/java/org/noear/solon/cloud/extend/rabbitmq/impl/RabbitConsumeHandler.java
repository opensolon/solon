package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.*;
import org.noear.snack.ONode;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.utils.ExpirationUtils;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumeHandler extends DefaultConsumer {
    Map<String, CloudEventObserverEntity> observerMap;
    RabbitConfig cfg;
    RabbitProducer producer;
    String eventChannelName;

    public RabbitConsumeHandler(RabbitProducer producer, RabbitConfig config, Channel channel, Map<String, CloudEventObserverEntity> observerMap) {
        super(channel);
        this.cfg = config;
        this.producer = producer;
        this.observerMap = observerMap;
        this.eventChannelName = RabbitmqProps.instance.getEventChannel();
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            String event_json = new String(body);
            Event event = ONode.deserialize(event_json, Event.class);
            event.channel(eventChannelName);

            CloudEventObserverEntity observer = observerMap.get(event.topic());
            boolean isHandled = true;

            if (observer != null) {
                isHandled = observer.handler(event);
            }

            if (isHandled == false) {
                event.times(event.times() + 1);

                try {
                    isHandled = producer.publish(event, cfg.queue_ready, ExpirationUtils.getExpiration(event.times()));
                } catch (Throwable ex) {
                    getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                    isHandled = true;
                }
            }

            if (isHandled) {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            }

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}