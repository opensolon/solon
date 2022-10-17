package org.noear.solon.cloud.extend.pulsar.service;

import org.apache.pulsar.client.api.*;
import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.pulsar.PulsarProps;
import org.noear.solon.cloud.extend.pulsar.impl.PulsarMessageListenerImpl;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author noear
 * @since 1.5
 */
public class CloudEventServicePulsarImp implements CloudEventServicePlus {

    private final CloudProps cloudProps;

    private PulsarClient client;

    public CloudEventServicePulsarImp(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        try {
            client = PulsarClient.builder()
                    .serviceUrl(cloudProps.getEventServer())
                    .build();
        } catch (PulsarClientException e) {
            throw new CloudEventException(e);
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
            topicNew = event.group() + PulsarProps.GROUP_SPLIT_MART + event.topic();
        }

        byte[] event_data = ONode.stringify(event).getBytes(StandardCharsets.UTF_8);


        try (Producer<byte[]> producer = client.newProducer().topic(topicNew).create()) {

            if (event.scheduled() == null) {
                producer.newMessage()
                        .key(event.key())
                        .value(event_data)
                        .send();
            } else {
                producer.newMessage()
                        .key(event.key())
                        .value(event_data)
                        .deliverAt(event.scheduled().getTime())
                        .send();
            }

            return true;
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
            topicNew = group + PulsarProps.GROUP_SPLIT_MART + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, observer);
    }

    public void subscribe() {
        if (observerManger.topicSize() > 0) {
            String consumerGroup = getEventConsumerGroup();

            if (Utils.isEmpty(consumerGroup)) {
                consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
            }

            try {
                client.newConsumer()
                        .topics(new ArrayList<>(observerManger.topicAll()))
                        .messageListener(new PulsarMessageListenerImpl(cloudProps, observerManger))
                        .subscriptionName(consumerGroup)
                        .subscriptionType(SubscriptionType.Shared)
                        .subscribe();
            } catch (Exception e) {
                throw new CloudEventException(e);
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

    /**
     * 消费组
     */
    public String getEventConsumerGroup() {
        return cloudProps.getValue(PulsarProps.PROP_EVENT_consumerGroup);
    }

    /**
     * 产品组
     */
    public String getEventProducerGroup() {
        return cloudProps.getValue(PulsarProps.PROP_EVENT_producerGroup);
    }
}
