package org.noear.solon.cloud.extend.folkmq.service;

import org.noear.folkmq.FolkMQ;
import org.noear.folkmq.client.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.folkmq.FolkmqProps;
import org.noear.solon.cloud.extend.folkmq.impl.FolkmqConsumeHandler;
import org.noear.solon.cloud.extend.folkmq.impl.FolkmqTransactionListener;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventObserver;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
public class CloudEventServiceFolkMqImpl implements CloudEventServicePlus {
    protected final MqClient client;

    private final CloudProps cloudProps;
    private final FolkmqConsumeHandler folkmqConsumeHandler;
    private final CloudEventObserverManger observerManger;
    private final long publishTimeout;

    public CloudEventServiceFolkMqImpl(CloudProps cloudProps) {
        this.cloudProps = cloudProps;
        this.observerManger = new CloudEventObserverManger();
        this.folkmqConsumeHandler = new FolkmqConsumeHandler(observerManger);
        this.publishTimeout = cloudProps.getEventPublishTimeout();

        this.client = FolkMQ.createClient(cloudProps.getEventServer())
                .nameAs(Solon.cfg().appName())
                .namespaceAs(cloudProps.getNamespace())
                .autoAcknowledge(false);

        if (publishTimeout > 0) {
            client.config(c -> c.requestTimeout(publishTimeout));
        }

        //总线扩展
        EventBus.publish(client);

        //加入容器
        Solon.context().wrapAndPut(MqClient.class, client);

        //异步获取 MqTransactionCheckback
        Solon.context().getBeanAsync(MqTransactionCheckback.class, bean -> {
            client.transactionCheckback(bean);
        });

        Solon.context().getBeanAsync(MqConsumeListener.class, bean -> {
            client.listen(bean);
        });


        try {
            client.connect();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    private void beginTransaction(EventTran transaction) {
        if (transaction.getListener(FolkmqTransactionListener.class) != null) {
            return;
        }

        transaction.setListener(new FolkmqTransactionListener(client.newTransaction()));
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        if (Utils.isEmpty(event.topic())) {
            throw new IllegalArgumentException("Event missing topic");
        }

        if (Utils.isEmpty(event.content())) {
            throw new IllegalArgumentException("Event missing content");
        }

        if (event.tran() != null) {
            beginTransaction(event.tran());
        }

        //new topic
        String topicNew = FolkmqProps.getTopicNew(event);
        try {
            MqMessage message = new MqMessage(event.content(), event.key())
                    .scheduled(event.scheduled())
                    .tag(event.tags())
                    .qos(event.qos());

            if (event.tran() != null) {
                MqTransaction transaction = event.tran().getListener(FolkmqTransactionListener.class).getTransaction();
                message.transaction(transaction);
            }

            if (publishTimeout > 0) {
                //同步
                client.publish(topicNew, message);
            } else {
                //异步
                client.publishAsync(topicNew, message);
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
                EventObserver observer = observerManger.getByTopic(topicNew);

                if (observer.getLevel() == EventLevel.instance) {
                    String instanceName = Instance.local().serviceAndAddress()
                            .replace("@","-")
                            .replace(".","_")
                            .replace(":","_");
                    //实例订阅
                    client.subscribe(topicNew, instanceName, folkmqConsumeHandler);
                } else {
                    //集群订阅
                    client.subscribe(topicNew, instance.service(), folkmqConsumeHandler);
                }
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