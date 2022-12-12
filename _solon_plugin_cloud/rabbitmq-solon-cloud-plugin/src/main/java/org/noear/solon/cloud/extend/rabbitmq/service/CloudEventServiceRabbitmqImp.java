package org.noear.solon.cloud.extend.rabbitmq.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitChannelFactory;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitConfig;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitConsumer;
import org.noear.solon.cloud.extend.rabbitmq.impl.RabbitProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;

/**
 *
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceRabbitmqImp implements CloudEventServicePlus {


    private final CloudProps cloudProps;
    private RabbitProducer producer;
    private RabbitConsumer consumer;

    public CloudEventServiceRabbitmqImp(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        try {
            RabbitConfig config = new RabbitConfig(cloudProps);
            config.server = cloudProps.getEventServer();
            config.username = cloudProps.getUsername();
            config.password = cloudProps.getPassword();

            RabbitChannelFactory factory = new RabbitChannelFactory(cloudProps, config);

            producer = new RabbitProducer(factory);
            consumer = new RabbitConsumer(cloudProps, producer, factory);

            producer.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
            topicNew = event.group() + RabbitmqProps.GROUP_SPLIT_MARK + event.topic();
        }

        try {
            return producer.publish(event, topicNew);
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
    }

    CloudEventObserverManger observerManger = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, CloudEventHandler observer) {
        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + RabbitmqProps.GROUP_SPLIT_MARK + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, observer);
    }

    public void subscribe() {
        try {
            if (observerManger.topicSize() > 0) {
                consumer.init(observerManger);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
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
