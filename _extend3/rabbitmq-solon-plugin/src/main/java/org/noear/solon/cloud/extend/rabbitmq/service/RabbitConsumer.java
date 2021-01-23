package org.noear.solon.cloud.extend.rabbitmq.service;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;

/**
 * @author noear 2021/1/24 created
 */
public class RabbitConsumer extends DefaultConsumer {
    CloudEventServiceImp eventServiceImp;
    String topic;

    public RabbitConsumer(Channel channel, String topic, CloudEventServiceImp eventServiceImp) {
        super(channel);
        this.topic = topic;
        this.eventServiceImp = eventServiceImp;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        Event event = new Event(topic, new String(body));

        try {
            eventServiceImp.onReceive(event);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }
}