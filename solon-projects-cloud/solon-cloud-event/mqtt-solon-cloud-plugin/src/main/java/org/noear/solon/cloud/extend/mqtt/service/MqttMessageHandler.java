package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息处理
 *
 * @author noear
 * @since 2.5
 */
public class MqttMessageHandler implements Runnable {
    private static Logger log = LoggerFactory.getLogger(MqttMessageListenerImpl.class);
    private MqttClientManager clientManager;
    private String eventChannelName;
    private CloudEventHandler eventHandler;
    private String topic;
    private MqttMessage message;

    public MqttMessageHandler(MqttClientManager clientManager, String eventChannelName, CloudEventHandler eventHandler, String topic, MqttMessage message) {
        this.clientManager = clientManager;
        this.eventChannelName = eventChannelName;
        this.eventHandler = eventHandler;
        this.topic = topic;
        this.message = message;
    }

    @Override
    public void run() {
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
