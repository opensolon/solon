package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.water.integration.WaterAdapter;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.water.WaterClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/1/17 created
 */
public class CloudEventServiceImp implements CloudEventService {
    Map<String, CloudEventHandler> eventHandlerMap = new HashMap<>();

    @Override
    public void send(String queue, String topic, String content) {
        try {
            WaterClient.Message.sendMessage(topic, content);
        } catch (Throwable ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    @Override
    public void send(String id, String queue, String topic, String content) {
        try {
            WaterClient.Message.sendMessage(id, topic, content);
        } catch (Throwable ex) {
            throw Utils.throwableWrap(ex);
        }
    }

    @Override
    public void attention(String queue, String topic, CloudEventHandler observer) {
        WaterAdapter.global().router().put(topic, (msg) -> {
            Event event = new Event();
            event.id = msg.key;
            event.topic = msg.topic;
            event.content = msg.message;
            event.tags = msg.tags;

            return observer.handler(event);
        });
    }
}
