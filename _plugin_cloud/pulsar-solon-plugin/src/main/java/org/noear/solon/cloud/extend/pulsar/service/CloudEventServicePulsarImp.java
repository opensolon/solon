package org.noear.solon.cloud.extend.pulsar.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.pulsar.PulsarProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventServicePlus;

/**
 * @author noear
 * @since 1.5
 */
public class CloudEventServicePulsarImp implements CloudEventServicePlus {
    @Override
    public boolean publish(Event event) throws CloudEventException {
        return false;
    }

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {

    }

    private String channel;
    private String group;

    @Override
    public String getChannel() {
        if (channel == null) {
            channel = PulsarProps.instance.getEventChannel();
        }
        return channel;
    }

    @Override
    public String getGroup() {
        if (group == null) {
            group = PulsarProps.instance.getEventGroup();
        }

        return group;
    }
}
