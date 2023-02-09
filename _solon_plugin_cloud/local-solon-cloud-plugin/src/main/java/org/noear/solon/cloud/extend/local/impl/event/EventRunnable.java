package org.noear.solon.cloud.extend.local.impl.event;

import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.local.service.CloudEventServiceLocalImpl;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.event.EventBus;

/**
 * @author noear
 * @since 1.12
 */
public class EventRunnable implements Runnable {
    CloudEventServiceLocalImpl eventService;
    Event event;

    public EventRunnable(CloudEventServiceLocalImpl eventService, Event event) {
        this.eventService = eventService;
        this.event = event;
    }

    @Override
    public void run() {
        try {
            //派发
            eventService.distribute(event);
        } catch (Throwable e) {
            EventBus.pushTry(new CloudJobException(e));
        }
    }
}
