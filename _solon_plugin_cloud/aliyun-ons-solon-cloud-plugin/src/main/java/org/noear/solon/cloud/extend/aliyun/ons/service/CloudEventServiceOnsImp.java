package org.noear.solon.cloud.extend.aliyun.ons.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.aliyun.ons.OnsProps;
import org.noear.solon.cloud.extend.aliyun.ons.impl.OnsConfig;
import org.noear.solon.cloud.extend.aliyun.ons.impl.OnsConsumer;
import org.noear.solon.cloud.extend.aliyun.ons.impl.OnsProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;

/**
 * @author cgy
 * @since 1.11
 */
public class CloudEventServiceOnsImp implements CloudEventServicePlus {
    private CloudProps cloudProps;
    private OnsProducer producer;
    private OnsConsumer consumer;

    public CloudEventServiceOnsImp(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        OnsConfig config = new OnsConfig(cloudProps);

        producer = new OnsProducer(config);
        consumer = new OnsConsumer(config);
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

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + OnsProps.GROUP_SPLIT_MARK + event.topic();
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
    public void attention(EventLevel level, String channel, String group, String topic, String tag, CloudEventHandler observer) {
        topic = topic.replace(".", "_");

        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + OnsProps.GROUP_SPLIT_MARK + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, observer);
    }

    public void subscribe() {
        if (observerManger.topicSize() > 0) {
            consumer.init(cloudProps, observerManger);
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
}