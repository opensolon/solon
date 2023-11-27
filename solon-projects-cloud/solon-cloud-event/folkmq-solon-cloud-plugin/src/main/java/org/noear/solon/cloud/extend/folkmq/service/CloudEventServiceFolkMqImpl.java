package org.noear.solon.cloud.extend.folkmq.service;

import org.noear.folkmq.FolkMQ;
import org.noear.folkmq.client.MqClient;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.folkmq.FolkmqProps;
import org.noear.solon.cloud.extend.folkmq.impl.FolkmqConsumeHandler;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 2.6
 */
public class CloudEventServiceFolkMqImpl implements CloudEventServicePlus {
    private final CloudProps cloudProps;
    private final MqClient client;
    private final FolkmqConsumeHandler folkmqConsumeHandler;
    private final CloudEventObserverManger observerManger;
    private final long publishTimeout;

    public CloudEventServiceFolkMqImpl(CloudProps cloudProps) {
        this.cloudProps = cloudProps;
        this.observerManger = new CloudEventObserverManger();
        this.folkmqConsumeHandler = new FolkmqConsumeHandler(observerManger);
        this.publishTimeout = cloudProps.getEventPublishTimeout();
        this.client = FolkMQ.createClient(cloudProps.getEventServer())
                .autoAcknowledge(false);
        try {
            client.connect();
        } catch (IOException e) {
            throw new IllegalStateException(e);
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

        //new topic
        String topicNew = FolkmqProps.getTopicNew(event);
        try {
            if (publishTimeout > 0) {
                //异步等待
                client.publish(topicNew, event.content(), event.scheduled(), event.qos())
                        .get(publishTimeout, TimeUnit.MILLISECONDS);
            } else {
                //异步
                client.publish(topicNew, event.content(), event.scheduled(), event.qos());
            }
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
        return true;
    }

    @Override
    public void attention(EventLevel level, String channel, String group,
                          String topic, String tag, int qos, CloudEventHandler observer) {
        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + FolkmqProps.GROUP_SPLIT_MARK + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, qos, observer);

    }

    public void subscribe() throws IOException {
        if (observerManger.topicSize() > 0) {
            Instance instance = Instance.local();

            for (String topicNew : observerManger.topicAll()) {
                client.subscribe(topicNew, instance.service(), folkmqConsumeHandler);
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
