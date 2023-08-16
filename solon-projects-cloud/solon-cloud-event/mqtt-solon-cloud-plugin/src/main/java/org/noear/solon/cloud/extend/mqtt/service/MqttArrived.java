package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;

/**
 * @author noear 2023/8/16 created
 */
public class MqttArrived {
    public static void messageArrived(Logger log, String eventChannelName, CloudEventHandler eventHandler, String topic, MqttMessage message) throws Exception {
        try {
            Event event = new Event(topic, new String(message.getPayload()))
                    .qos(message.getQos())
                    .retained(message.isRetained())
                    .channel(eventChannelName);

            if (eventHandler != null) {
                eventHandler.handle(event);
            } else {
                //只需要记录一下
                log.warn("There is no observer for this event topic[{}]", event.topic());
            }
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);

            EventBus.publishTry(ex);

            if (ex instanceof Exception) {
                throw (Exception) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
}
