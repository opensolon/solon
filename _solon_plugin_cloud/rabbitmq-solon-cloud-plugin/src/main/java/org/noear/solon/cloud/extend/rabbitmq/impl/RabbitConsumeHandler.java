package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.*;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.utils.ExpirationUtils;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumeHandler extends DefaultConsumer {
    static Logger log = LoggerFactory.getLogger(RabbitConsumeHandler.class);

    CloudEventObserverManger observerManger;
    RabbitConfig cfg;
    RabbitProducer producer;
    String eventChannelName;

    public RabbitConsumeHandler(CloudProps cloudProps, RabbitProducer producer, RabbitConfig config, Channel channel, CloudEventObserverManger observerManger) {
        super(channel);
        this.cfg = config;
        this.producer = producer;
        this.observerManger = observerManger;
        this.eventChannelName = cloudProps.getEventChannel();
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            String event_json = new String(body);
            Event event = ONode.deserialize(event_json, Event.class);
            event.channel(eventChannelName);

            boolean isOk = onReceive(event); //吃掉异常，方便下面的动作

            if (isOk == false) {
                event.times(event.times() + 1);

                try {
                    isOk = producer.publish(event, cfg.queue_ready, ExpirationUtils.getExpiration(event.times()));
                } catch (Throwable ex) {
                    getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                    isOk = true;
                }
            }

            if (isOk) {
                getChannel().basicAck(envelope.getDeliveryTag(), false);
            }

        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);

            EventBus.push(e);

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean onReceive(Event event) throws Throwable {
        try {
            return onReceiveDo(event);
        } catch (Throwable e) {
            EventBus.push(e);
            return false;
        }
    }

    /**
     * 处理接收事件
     */
    private boolean onReceiveDo(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + RabbitmqProps.GROUP_SPLIT_MARK + event.topic();
        }

        handler = observerManger.getByTopic(topicNew);
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }
}