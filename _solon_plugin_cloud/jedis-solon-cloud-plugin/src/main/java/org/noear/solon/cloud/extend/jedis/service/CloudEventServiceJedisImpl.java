package org.noear.solon.cloud.extend.jedis.service;

import org.noear.redisx.RedisClient;
import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.noear.solon.core.Props;

/**
 * 分布式事件适配
 *
 * @author noear
 * @since 1.10
 */
public class CloudEventServiceJedisImpl implements CloudEventServicePlus {
    private final RedisClient client;
    private CloudProps cloudProps;

    public CloudEventServiceJedisImpl(RedisClient client) {
        this.client = client;
    }

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
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        client.open(s -> s.publish(event.topic(), ONode.stringify(event)));
        return true;
    }

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        client.open(s -> s.subscribe(new JedisPubSubImpl(channel, group, topic, observer), topic));
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
