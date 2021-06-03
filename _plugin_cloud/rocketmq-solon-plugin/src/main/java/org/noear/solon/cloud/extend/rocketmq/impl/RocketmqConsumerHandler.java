package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConsumerHandler implements MessageListenerConcurrently {
    static Logger log = LoggerFactory.getLogger(RocketmqConsumerHandler.class);

    Map<String, CloudEventObserverEntity> observerMap;
    String eventChannelName;

    public RocketmqConsumerHandler(Map<String, CloudEventObserverEntity> observers) {
        observerMap = observers;
        eventChannelName = RocketmqProps.instance.getEventChannel();
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        boolean isOk = true;

        try {
            for (MessageExt message : list) {
                Event event = new Event(message.getTopic(), new String(message.getBody()));
                event.tags(message.getTags());
                event.key(message.getKeys());
                event.times(message.getReconsumeTimes());
                event.channel(eventChannelName);

                isOk = isOk && onReceive(event);
            }
        } catch (Throwable ex) {
            EventBus.push(ex);
        }

        if (isOk) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } else {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
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
        }else{
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", event.topic());
        }

        return isOk;
    }
}
