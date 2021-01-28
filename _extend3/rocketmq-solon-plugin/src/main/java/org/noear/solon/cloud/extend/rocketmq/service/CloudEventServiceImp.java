package org.noear.solon.cloud.extend.rocketmq.service;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.rocketmq.impl.MessageUtil;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.core.event.EventBus;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceImp implements CloudEventService, MessageListenerConcurrently {
    String server;
    String group;

    DefaultMQProducer producer;
    DefaultMQPushConsumer consumer;

    public CloudEventServiceImp(String server) {
        this.server = server;
        this.group = Solon.cfg().appGroup();

        if (Utils.isEmpty(group)) {
            group = "DEFAULT_GROUP";
        }
    }

    private void initProducer() {
        if (producer != null) {
            return;
        }

        synchronized (group) {
            if (producer != null) {
                return;
            }

            producer = new DefaultMQProducer(group);
            producer.setNamesrvAddr(server);
            //发送超时时间，默认3000 单位ms
            producer.setSendMsgTimeout(3000);

            try {
                producer.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void initConsumer(Set<String> topics) {
        if (consumer != null) {
            return;
        }

        synchronized (group) {
            if (consumer != null) {
                return;
            }

            consumer = new DefaultMQPushConsumer(group);

            consumer.setNamesrvAddr(server);
            //一次最大消费的条数
            consumer.setPullBatchSize(1);
            //无消息时，最大阻塞时间。默认5000 单位ms
            try {
                //要消费的topic，可使用tag进行简单过滤
                for (String topic : topics) {
                    consumer.subscribe(topic, "*");
                }

                consumer.registerMessageListener(this);
                consumer.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }


    @Override
    public boolean publish(Event event) {
        initProducer();

        try {
            Message message = MessageUtil.buildNewMeaage(event);

            SendResult send = producer.send(message);

            if (send.getSendStatus().equals(SendStatus.SEND_OK)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        topic = topic.replace(".", "-");

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        initConsumer(observerMap.keySet());
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
}
