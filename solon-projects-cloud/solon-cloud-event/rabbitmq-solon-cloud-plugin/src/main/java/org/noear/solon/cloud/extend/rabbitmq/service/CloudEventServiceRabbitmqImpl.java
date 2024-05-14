package org.noear.solon.cloud.extend.rabbitmq.service;

import com.rabbitmq.client.Channel;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.extend.rabbitmq.impl.*;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceRabbitmqImpl implements CloudEventServicePlus {
    private static final Logger log = LoggerFactory.getLogger(CloudEventServiceRabbitmqImpl.class);

    private final CloudProps cloudProps;
    private RabbitProducer producer;
    private RabbitConsumer consumer;
    private RabbitChannelFactory factory;

    public CloudEventServiceRabbitmqImpl(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        try {
            RabbitConfig config = new RabbitConfig(cloudProps);
            factory = new RabbitChannelFactory(config);

            Channel channel = factory.createChannel(false);

            producer = new RabbitProducer(config, channel);
            consumer = new RabbitConsumer(config, channel, producer);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void beginTransaction(EventTran transaction) throws CloudEventException {
        if (transaction.getListener(RabbitTransactionListener.class) != null) {
            return;
        }

        try {
            Channel channelTx = factory.createChannel(true);
            channelTx.txSelect();
            transaction.setListener(new RabbitTransactionListener(channelTx));
        } catch (Exception e) {
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

        if(event.tran()!=null){
            beginTransaction(event.tran());
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
    public void attention(EventLevel level, String channel, String group, String topic, String tag, int qos, CloudEventHandler observer) {
        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + RabbitmqProps.GROUP_SPLIT_MARK + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, qos, observer);
    }

    public void subscribe() {
        if (observerManger.topicSize() > 0) {
            try {
                consumer.init(observerManger);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
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
}
