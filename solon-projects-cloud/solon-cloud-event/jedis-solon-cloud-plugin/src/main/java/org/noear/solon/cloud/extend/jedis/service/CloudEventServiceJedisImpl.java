package org.noear.solon.cloud.extend.jedis.service;

import org.noear.redisx.RedisClient;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.jedis.JedisProps;
import org.noear.solon.cloud.extend.jedis.impl.JedisEventConsumer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.noear.solon.core.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分布式事件适配
 *
 * @author noear
 * @since 1.10
 */
public class CloudEventServiceJedisImpl implements CloudEventServicePlus {
    private static final Logger log = LoggerFactory.getLogger(CloudEventServiceJedisImpl.class);

    private final RedisClient client;
    private final CloudProps cloudProps;


    public CloudEventServiceJedisImpl(CloudProps cloudProps) {
        Props props = cloudProps.getProp("event");

        if (props.get("server") == null) {
            props.putIfNotNull("server", cloudProps.getServer());
        }

        if (props.get("user") == null) {
            props.putIfNotNull("user", cloudProps.getUsername());
        }

        if (props.get("password") == null) {
            props.putIfNotNull("password", cloudProps.getPassword());
        }

        this.client = new RedisClient(props);
        this.cloudProps = cloudProps;
    }

    private void beginTransaction(EventTran transaction) throws CloudEventException {
        //不支持事务消息
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

        if(event.tran() != null){
            beginTransaction(event.tran());
        }

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + JedisProps.GROUP_SPLIT_MARK + event.topic();
        }

        client.open(s -> s.publish(topicNew, ONode.stringify(event)));
        return true;
    }

    CloudEventObserverManger observerManger = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, int qos, CloudEventHandler observer) {
        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + JedisProps.GROUP_SPLIT_MARK + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, qos, observer);
    }

    public void subscribe() {
        if (observerManger.topicSize() > 0) {
            try {
                String[] topicAll = new String[observerManger.topicAll().size()];
                observerManger.topicAll().toArray(topicAll);

                //用异步处理
                new Thread(() -> {
                    client.open(s -> s.subscribe(new JedisEventConsumer(cloudProps, observerManger), topicAll));
                }).start();
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public String getChannel() {
        if (cloudProps == null) {
            return null;
        } else {
            return cloudProps.getEventChannel();
        }
    }

    @Override
    public String getGroup() {
        if (cloudProps == null) {
            return null;
        } else {
            return cloudProps.getEventGroup();
        }
    }
}
