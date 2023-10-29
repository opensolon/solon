package org.noear.solon.cloud.extend.mqtt5.service;

import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;

/**
 * @author noear
 * @since 2.4
 */
public class MqttUtil {
    /**
     * 接收
     */
    public static void receive(MqttClientManager clientManager, Logger log, String eventChannelName, CloudEventHandler eventHandler, String topic, MqttMessage message) throws Exception {
        try {
            Event event = new Event(topic, new String(message.getPayload()))
                    .qos(message.getQos())
                    .retained(message.isRetained())
                    .channel(eventChannelName);

            if (eventHandler != null) {
                if (eventHandler.handle(event)) {
                    //手动 ack
                    clientManager.getClient().messageArrivedComplete(message.getId(), message.getQos());
                }
            } else {
                //手动 ack
                clientManager.getClient().messageArrivedComplete(message.getId(), message.getQos());
                //记录一下它是没有订阅的
                log.warn("There is no observer for this event topic[{}]", event.topic());
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            //不返回异常，不然会关掉客户端（已使用手动ack）
            log.warn(e.getMessage(), e);
        }
    }
}
