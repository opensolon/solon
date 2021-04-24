package org.noear.solon.cloud.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventManager implements CloudEventService {
    Map<String, CloudEventService> route = new HashMap<>();

    public void register(String channel, CloudEventService service) {
        if (channel == null) {
            route.put("", service);
        } else {
            route.put(channel, service);
        }
    }

    public CloudEventService get(String channel) {
        if (channel == null) {
            channel = "";
        }

        return route.get(channel);
    }

    protected CloudEventService getOrThrow(String channel) {
        CloudEventService tmp = get(channel);

        if (tmp == null) {
            if (Utils.isEmpty(channel)) {
                throw new RuntimeException("CloudEventService does not exist");
            } else {
                throw new RuntimeException("CloudEventService does not exist channel &" + channel);
            }
        }

        return tmp;
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        return getOrThrow(event.channel()).publish(event);
    }

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        getOrThrow(channel).attention(level, channel, group, topic, observer);
    }
}
