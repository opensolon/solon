package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.*;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.EventObserver;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 */
public class MqttCallbackImpl implements MqttCallback {
    static Logger log = LoggerFactory.getLogger(MqttCallbackImpl.class);

    final MqttClient client;
    final CloudEventObserverManger observerManger;
    final String eventChannelName;

    public MqttCallbackImpl(MqttClient client, CloudEventObserverManger observerManger, CloudProps cloudProps) {
        this.client = client;
        this.observerManger = observerManger;
        this.eventChannelName = cloudProps.getEventChannel();
    }

    //在断开连接时调用
    @Override
    public void connectionLost(Throwable e) {
        log.warn(e.getMessage(), e);
    }

    //已经预订的消息
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        CloudEventHandler eventHandler = observerManger.getByTopic(topic);

        MqttUtil.receive(log, eventChannelName, eventHandler, topic, message);
    }

    //发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
