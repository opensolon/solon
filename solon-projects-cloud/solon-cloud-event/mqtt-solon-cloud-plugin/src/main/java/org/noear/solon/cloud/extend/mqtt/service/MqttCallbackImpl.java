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
class MqttCallbackImpl implements MqttCallback {
    static Logger log = LoggerFactory.getLogger(MqttCallbackImpl.class);

    final MqttClient client;
    final String eventChannelName;

    public MqttCallbackImpl(MqttClient client, CloudProps cloudProps) {
        this.client = client;
        this.eventChannelName = cloudProps.getEventChannel();
    }

    CloudEventObserverManger observerManger;

    public void subscribe(CloudEventObserverManger observerManger) throws MqttException {
        this.observerManger = observerManger;

        String[] topicAry = observerManger.topicAll().toArray(new String[0]);
        int[] topicQos = new int[topicAry.length];
        IMqttMessageListener[] topicListener = new IMqttMessageListener[topicAry.length];
        for (int i = 0, len = topicQos.length; i < len; i++) {
            EventObserver eventObserver = observerManger.getByTopic(topicAry[i]);
            topicQos[i] = eventObserver.getQos();
            topicListener[i] = new MqttMessageListenerImpl(eventChannelName, eventObserver);
        }

        client.subscribe(topicAry, topicQos, topicListener);
    }

    //在断开连接时调用
    @Override
    public void connectionLost(Throwable e) {
        EventBus.publishTry(e);
    }

    //已经预订的消息
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        CloudEventHandler eventHandler = observerManger.getByTopic(topic);

        MqttArrived.messageArrived(log, eventChannelName, eventHandler, topic, message);
    }

    //发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
