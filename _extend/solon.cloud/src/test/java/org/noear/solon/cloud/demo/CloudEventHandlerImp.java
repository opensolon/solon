package org.noear.solon.cloud.demo;

import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.CloudEvent;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear 2021/1/14 created
 */
@CloudEvent("water.config.update")
public class CloudEventHandlerImp implements CloudEventHandler {
    @Override
    public boolean handler(Event event) {
        return false;
    }
}
