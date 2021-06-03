package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.*;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.utils.ExpirationUtils;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumeHandler extends DefaultConsumer {
    static Logger log = LoggerFactory.getLogger(RabbitConsumeHandler.class);

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

            boolean isOk = onReceive(event);

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

        } catch (Throwable ex) {
            EventBus.push(ex);

            //按协议要求，抛到上一级
            if (ex instanceof IOException) {
                throw (IOException) ex;
            } else {
                throw Utils.throwableWrap(ex);
            }
        }
    }

    /**
     * 处理接收事件
     */
    public boolean onReceive(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventObserverEntity entity = null;

        entity = observerMap.get(event.topic());
        if (entity != null) {
            isOk = entity.handler(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", event.topic());
        }

        return isOk;
    }
}