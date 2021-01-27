package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.*;
import org.noear.snack.ONode;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
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

    public RabbitConsumeHandler(RabbitProducer producer, RabbitConfig config, Channel channel, Map<String, CloudEventObserverEntity> observerMap) {
        super(channel);
        this.cfg = config;
        this.producer = producer;
        this.observerMap = observerMap;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            String event_json = new String(body);
            Event event = ONode.deserialize(event_json, Event.class);

            CloudEventObserverEntity observer = observerMap.get(event.topic());
            boolean isOk = false;

            if (observer != null) {
                //如果
                isOk = observer.handler(event);
            }

            if (isOk == false) {
                event.times(event.times() + 1);

                try {
                    //
                    //手动应签模式
                    //
                    //long deliveryTag, boolean multiple
                    producer.publish(event, cfg.queue_ready);
                } catch (Throwable ex) {
                    //long deliveryTag, boolean multiple, boolean requeue
                    getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                }
            }

            if (isOk) {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            }

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}