package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;

/**
 * @author noear 2021/4/20 created
 */
public class CloudEventRoute implements CloudEventService {
    @Override
    public boolean publish(Event event) {
        return false;
    }

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {

    }
}
