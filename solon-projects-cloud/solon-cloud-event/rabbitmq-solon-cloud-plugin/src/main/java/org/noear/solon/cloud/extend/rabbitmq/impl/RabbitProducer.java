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
    private long timeout;
    private RabbitChannelFactory factory;
    private AMQP.BasicProperties eventPropsDefault;

    public RabbitProducer(RabbitChannelFactory factory) {
        this.config = factory.getConfig();
        this.factory = factory;
        this.eventPropsDefault = newEventProps().build();
        this.timeout = factory.getCloudProps().getEventPublishTimeout();
    }

    public AMQP.BasicProperties.Builder newEventProps() {
        return new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .contentType("application/json");
    }

    /**
     * 初始化
     */
    private void init() throws IOException, TimeoutException {
        if(channel == null) {
            synchronized (factory){
                if(channel == null){
                    channel = factory.getChannel();

                    Map<String, Object> args = new HashMap<>();

                    channel.exchangeDeclare(config.exchangeName,
                            config.exchangeType,
                            config.durable,
                            config.autoDelete,
                            config.internal, args);

                    if (timeout > 0) {
                        channel.confirmSelect();
                    }
                }
            }
        }
    }

    public boolean publish(Event event, String topic, long delay) throws Exception {
        init();

        byte[] event_data = ONode.stringify(event).getBytes(StandardCharsets.UTF_8);

        AMQP.BasicProperties props;
        if (delay > 0) {
            props = newEventProps().expiration(String.valueOf(delay)).build();
        } else {
            props = eventPropsDefault;
        }

        channel.basicPublish(config.exchangeName, topic, config.mandatory, props, event_data);

        if (timeout > 0) {
            return channel.waitForConfirms(timeout);
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
