package org.noear.solon.cloud.extend.rabbitmq.service;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceImp implements CloudEventService {
    @Override
    public boolean push(Event event) {
        return false;
    }

    @Override
    public void attention(EventLevel level, String queue, String topic, CloudEventHandler observer) {

    }
}
