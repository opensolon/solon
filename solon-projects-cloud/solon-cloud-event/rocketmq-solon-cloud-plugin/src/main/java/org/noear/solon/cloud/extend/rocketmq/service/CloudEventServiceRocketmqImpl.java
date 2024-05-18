package org.noear.solon.cloud.extend.rocketmq.service;

import org.apache.rocketmq.client.exception.MQClientException;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqConfig;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqConsumer;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceRocketmqImpl implements CloudEventServicePlus , Closeable {
    private static final Logger log = LoggerFactory.getLogger(CloudEventServiceRocketmqImpl.class);

    private CloudProps cloudProps;
    private RocketmqProducer producer;
    private RocketmqConsumer consumer;

    public CloudEventServiceRocketmqImpl(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        RocketmqConfig config = new RocketmqConfig(cloudProps);

        producer = new RocketmqProducer(config);
        consumer = new RocketmqConsumer(config);
    }

    private void beginTransaction(EventTran transaction) throws CloudEventException {
        //4.0的事务与本地事务耦合太高，不好适配
        log.warn("Event transactions are not supported!");
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        if (Utils.isEmpty(event.topic())) {
            throw new IllegalArgumentException("Event missing topic");
        }

        if (Utils.isEmpty(event.content())) {
            throw new IllegalArgumentException("Event missing content");
        }

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        if (event.tran() != null) {
            beginTransaction(event.tran());
        }

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + RocketmqProps.GROUP_SPLIT_MARK + event.topic();
        }

        topicNew = topicNew.replace(".", "_");

        try {
            return producer.publish(event, topicNew);
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
    }


    CloudEventObserverManger observerManger = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, int qos, CloudEventHandler observer) {
        topic = topic.replace(".", "_");

        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + RocketmqProps.GROUP_SPLIT_MARK + topic;
        }

        if (Utils.isEmpty(tag)) {
            tag = "*";
        }

        observerManger.add(topicNew, level, group, topic, tag, qos, observer);
    }

    public void subscribe() {
        if (observerManger.topicSize() > 0) {
            try {
                consumer.init(observerManger);
            } catch (MQClientException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private String channel;
    private String group;

    @Override
    public String getChannel() {
        if (channel == null) {
            channel = cloudProps.getEventChannel();
        }
        return channel;
    }

    @Override
    public String getGroup() {
        if (group == null) {
            group = cloudProps.getEventGroup();
        }

        return group;
    }

    @Override
    public void close() throws IOException {
        if (consumer != null) {
            consumer.close();
        }

        if (producer != null) {
            producer.close();
        }
    }
}