package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.noear.snack.ONode;
import org.noear.solon.cloud.model.Event;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 *
 * @author noear
 * @since 1.3
 */
public class RabbitProducer {
    private RabbitConfig config;
    private Channel channel;
    private RabbitChannelFactory factory;
    private AMQP.BasicProperties eventPropsDefault;

    public RabbitProducer(RabbitChannelFactory factory) {
        this.config = factory.getConfig();
        this.factory = factory;
        this.eventPropsDefault = newEventProps().build();
    }

    public AMQP.BasicProperties.Builder newEventProps() {
        return new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .contentType("application/json");
    }

    /**
     * 绑定
     */
    public void bind() throws IOException, TimeoutException {
        channel = factory.getChannel();

        Map<String, Object> args = new HashMap<>();

        channel.exchangeDeclare(config.exchangeName,
                config.exchangeType,
                config.durable,
                config.autoDelete,
                config.internal, args);
    }

    public void publish(Event event, String topic, long ttl) throws IOException {
        byte[] event_data = ONode.stringify(event).getBytes(StandardCharsets.UTF_8);

        AMQP.BasicProperties props;
        if (ttl > 0) {
            props = newEventProps().expiration(String.valueOf(ttl)).build();
        } else {
            props = eventPropsDefault;
        }

        channel.basicPublish(config.exchangeName, topic, config.mandatory, props, event_data);
    }

    /**
     * 发布事件
     */
    public void publish(Event event) throws IOException {
        long ttl = 0;
        if (event.scheduled() != null) {
            ttl = event.scheduled().getTime() - System.currentTimeMillis();
        }

        if (ttl > 0) {
            publish(event, config.queue_ready, 0);
        } else {
            if (config.exchangeType == BuiltinExchangeType.FANOUT) {
                publish(event, "", 0);
            } else {
                publish(event, event.topic(), 0);
            }
        }
    }
}
