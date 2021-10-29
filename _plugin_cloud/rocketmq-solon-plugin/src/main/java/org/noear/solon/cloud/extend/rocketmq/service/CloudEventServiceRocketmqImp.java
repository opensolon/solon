package org.noear.solon.cloud.extend.rocketmq.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqConfig;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqConsumer;
import org.noear.solon.cloud.extend.rocketmq.impl.RocketmqProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceRocketmqImp implements CloudEventServicePlus {
    private static CloudEventServiceRocketmqImp instance;

    public static synchronized CloudEventServiceRocketmqImp getInstance() {
        if (instance == null) {
            instance = new CloudEventServiceRocketmqImp();
        }

        return instance;
    }


    RocketmqProducer producer;
    RocketmqConsumer consumer;

    private CloudEventServiceRocketmqImp() {
        RocketmqConfig config = new RocketmqConfig();

        producer = new RocketmqProducer(config);
        consumer = new RocketmqConsumer(config);
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        if (Utils.isEmpty(event.topic())) {
            throw new IllegalArgumentException("Event missing topic");
        }

        if (Utils.isEmpty(event.content())) {
            throw new IllegalArgumentException("Event missing content");
        }

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + RocketmqProps.GROUP_SPLIT_MART + event.topic();
        }

        topicNew = topicNew.replace(".", "_");

        try {
            return producer.publish(event, topicNew);
        }catch (Throwable ex){
            throw new CloudEventException(ex);
        }
    }


    CloudEventObserverManger observerManger = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        topic = topic.replace(".", "_");

        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + RocketmqProps.GROUP_SPLIT_MART + topic;
        }

        observerManger.add(topicNew, level, group, topic, observer);
    }

    public void subscribe() {
        if (observerManger.topicSize() > 0) {
            consumer.init(observerManger);
        }
    }

    private String channel;
    private String group;

    @Override
    public String getChannel() {
        if (channel == null) {
            channel = RocketmqProps.instance.getEventChannel();
        }
        return channel;
    }

    @Override
    public String getGroup() {
        if (group == null) {
            group = RocketmqProps.instance.getEventGroup();
        }

        return group;
    }
}