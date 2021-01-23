package org.noear.solon.cloud.extend.rocketmq.service;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceImp implements CloudEventService {
    DefaultMQProducer producer;
    DefaultLitePullConsumer consumer;

    public CloudEventServiceImp(){

    }


    @Override
    public void push(Event event) {

    }

    @Override
    public void attention(EventLevel level, String queue, String topic, CloudEventHandler observer) {

    }
}
