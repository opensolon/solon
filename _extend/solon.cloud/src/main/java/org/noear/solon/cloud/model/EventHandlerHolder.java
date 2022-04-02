package org.noear.solon.cloud.model;

import org.noear.solon.cloud.CloudEventHandler;

/**
 * @author noear
 * @since 1.6
 */
public class EventHandlerHolder implements CloudEventHandler {
    CloudEventHandler handler;

    public EventHandlerHolder(CloudEventHandler handler) {
        this.handler = handler;
    }

    /**
     * ::起到代理作用，从而附加能力
     * */
    @Override
    public boolean handler(Event event) throws Throwable {
        return false;
    }
}
