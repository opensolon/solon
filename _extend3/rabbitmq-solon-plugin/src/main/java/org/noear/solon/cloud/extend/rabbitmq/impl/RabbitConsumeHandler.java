package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumeHandler extends DefaultConsumer {
    CloudEventObserverEntity observer;
    RabbitConfig config;

    public RabbitConsumeHandler(RabbitConfig config, Channel channel, CloudEventObserverEntity observer) {
        super(channel);
        this.config = config;
        this.observer = observer;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            Event event = new Event(observer.topic, new String(body));

            observer.handler(event);

            if (config.consume_autoAck == false) {
                //
                //手动应签模式
                //
                // basicAck(long deliveryTag, boolean multiple)
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            }

        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}