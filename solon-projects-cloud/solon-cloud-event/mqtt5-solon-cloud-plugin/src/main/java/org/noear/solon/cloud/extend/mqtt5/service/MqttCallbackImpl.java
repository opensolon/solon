package org.noear.solon.cloud.extend.mqtt5.service;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 2.4
 */
public class MqttCallbackImpl implements MqttCallback {
    static final Logger log = LoggerFactory.getLogger(MqttCallbackImpl.class);

    final MqttClient client;
    final CloudEventObserverManger observerManger;
    final String eventChannelName;

    public MqttCallbackImpl(MqttClient client, CloudEventObserverManger observerManger, CloudProps cloudProps) {
        this.client = client;
        this.observerManger = observerManger;
        this.eventChannelName = cloudProps.getEventChannel();
    }

    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {

    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        log.warn(e.getMessage(), e);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        CloudEventHandler eventHandler = observerManger.getByTopic(topic);

        MqttUtil.receive(log, eventChannelName, eventHandler, topic, message);
    }

    @Override
    public void deliveryComplete(IMqttToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {

    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {

    }
}
