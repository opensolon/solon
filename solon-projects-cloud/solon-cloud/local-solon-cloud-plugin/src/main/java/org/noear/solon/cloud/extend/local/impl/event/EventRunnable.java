package org.noear.solon.cloud.extend.local.impl.event;

import org.noear.solon.cloud.extend.local.service.CloudEventServiceLocalImpl;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.12
 */
public class EventRunnable implements Runnable {
    static final Logger log = LoggerFactory.getLogger(EventRunnable.class);

    private CloudEventServiceLocalImpl eventService;
    private Event event;

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
            log.warn(e.getMessage(), e);
        }
    }
}
