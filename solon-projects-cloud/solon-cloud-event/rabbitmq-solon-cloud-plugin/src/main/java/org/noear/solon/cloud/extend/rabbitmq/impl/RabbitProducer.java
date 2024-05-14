package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.noear.snack.ONode;
import org.noear.solon.cloud.model.Event;

import java.nio.charset.StandardCharsets;

/**
 * 生产者
 *
 * @author noear
 * @since 1.3
 * @since 2.4
 */
public class RabbitProducer {
    private RabbitConfig config;
    private Channel channelDefault;
    private AMQP.BasicProperties eventPropsDefault;

    public RabbitProducer(RabbitConfig config, Channel channel) {
        this.config = config;
        this.channelDefault = channel;
        this.eventPropsDefault = newEventProps().build();
    }

    public AMQP.BasicProperties.Builder newEventProps() {
        return new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .contentType("application/json");
    }

    public boolean publish(Event event, String topic, long delay) throws Exception {
        byte[] event_data = ONode.stringify(event).getBytes(StandardCharsets.UTF_8);

        AMQP.BasicProperties props;
        if (delay > 0) {
            props = newEventProps().expiration(String.valueOf(delay)).build();
        } else {
            props = eventPropsDefault;
        }

        Channel channel = null;
        if (event.tran() == null) {
            channel = channelDefault;
        } else {
            channel = event.tran().getListener(RabbitTransactionListener.class).getTransaction();
        }

        channel.basicPublish(config.exchangeName, topic, config.mandatory, props, event_data);

        if (config.publishTimeout > 0  && event.tran() == null) {
            return channel.waitForConfirms(config.publishTimeout);
        } else {
            return true;
        }
    }

    /**
     * 发布事件
     */
    public boolean publish(Event event, String topic) throws Exception {
        long delay = 0;
        if (event.scheduled() != null) {
            delay = event.scheduled().getTime() - System.currentTimeMillis();
        }

        if (delay > 0) {
            return publish(event, config.queue_ready, delay);
        } else {
            if (config.exchangeType == BuiltinExchangeType.FANOUT) {
                return publish(event, "", 0);
            } else {
                return publish(event, topic, 0);
            }
        }
    }
}