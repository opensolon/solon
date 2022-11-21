package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventServicePlus;

/**
 * @author noear
 * @since 1.10
 */
public class CloudEventServiceLocalImpl implements CloudEventServicePlus {
    @Override
    public boolean publish(Event event) throws CloudEventException {
        return false;
    }

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {

    }

    @Override
    public String getChannel() {
        return "local";
    }

    @Override
    public String getGroup() {
        return Solon.cfg().appGroup();
    }
}
