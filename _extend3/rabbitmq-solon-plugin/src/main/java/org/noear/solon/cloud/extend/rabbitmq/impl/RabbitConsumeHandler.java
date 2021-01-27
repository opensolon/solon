package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
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
    RabbitConfig config;

    public RabbitConsumeHandler(RabbitConfig config, Channel channel, Map<String, CloudEventObserverEntity> observerMap) {
        super(channel);
        this.config = config;
        this.observerMap = observerMap;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            String topic = (String) properties.getHeaders().get("topic");

            CloudEventObserverEntity observer = observerMap.get(topic);
            boolean isOk = false;

            if (observer != null) {
                Event event = new Event(topic, new String(body));

                //如果
                isOk = observer.handler(event);
            }

            if (config.consume_autoAck == false && isOk) {
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