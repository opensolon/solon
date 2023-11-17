package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.*;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.util.RunUtil;

import java.io.IOException;

/**
 * 消费者接收处理
 *
 * @author noear
 * @since 1.3
 * @since 2.6
 */
public class RabbitConsumeHandler extends DefaultConsumer {

    protected final CloudEventObserverManger observerManger;
    protected final RabbitConfig config;
    protected final RabbitProducer producer;
    protected final String eventChannelName;

    public RabbitConsumeHandler(RabbitProducer producer, RabbitConfig config, Channel channel, CloudEventObserverManger observerManger) {
        super(channel);
        this.config = config;
        this.producer = producer;
        this.observerManger = observerManger;
        this.eventChannelName = config.getEventChannel();
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        RunUtil.async(new RabbitConsumeTask(this, consumerTag, envelope, properties, body));
    }
}