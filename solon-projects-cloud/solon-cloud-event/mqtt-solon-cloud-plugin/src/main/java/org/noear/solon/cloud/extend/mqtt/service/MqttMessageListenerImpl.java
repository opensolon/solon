package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.noear.solon.cloud.CloudEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 2.4
 */
class MqttMessageListenerImpl implements IMqttMessageListener {
    static Logger log = LoggerFactory.getLogger(MqttCallbackImpl.class);

    CloudEventHandler eventHandler;
    String eventChannelName;

    public MqttMessageListenerImpl(String eventChannelName, CloudEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.eventChannelName = eventChannelName;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        MqttArrived.messageArrived(log, eventChannelName, eventHandler, topic, message);
    }
}
