package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.core.event.EventBus;

import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConsumerHandler implements MessageListenerConcurrently {
    Map<String, CloudEventObserverEntity> observerMap;

    public RocketmqConsumerHandler(Map<String, CloudEventObserverEntity> observers){
        observerMap = observers;
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        boolean isOk = true;

        try {
            for (MessageExt message : list) {
                Event event = new Event(message.getTopic(), new String(message.getBody()));
                event.tags(message.getTags());
                event.key(message.getKeys());

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
        }

        return isOk;
    }
}
