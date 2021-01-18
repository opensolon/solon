package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.water.integration.WaterAdapter;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.water.WaterClient;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceImp implements CloudEventService {
    @Override
    public void send(Event event) {
        if(Utils.isEmpty(event.topic)){
            throw new IllegalArgumentException("Event missing topic");
        }

        if(Utils.isEmpty(event.content)){
            throw new IllegalArgumentException("Event missing content");
        }

        try {
            WaterClient.Message.sendMessageAndTags(event.uuid, event.topic, event.content, event.scheduled, event.tags);
        } catch (Throwable ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    @Override
    public void attention(String queue, String topic, CloudEventHandler observer) {
        WaterAdapter.global().router().put(topic, (msg) -> {
            Event event = new Event();
            event.uuid = msg.key;
            event.topic = msg.topic;
            event.content = msg.message;
            event.tags = msg.tags;

            return observer.handler(event);
        });
    }
}
