package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.*;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
class MqttCallbackImp implements MqttCallback {
    static Logger log = LoggerFactory.getLogger(MqttCallbackImp.class);

    final MqttClient client;

    public MqttCallbackImp(MqttClient client) {
        this.client = client;
    }

    Map<String, CloudEventObserverEntity> observerMap;

    public void subscribe(Map<String, CloudEventObserverEntity> observerMap) throws MqttException {
        this.observerMap = observerMap;

        String[] topicAry = observerMap.keySet().toArray(new String[0]);
        int[] topicQos = new int[topicAry.length];
        for (int i = 0, len = topicQos.length; i < len; i++) {
            topicQos[i] = 1;
        }

        client.subscribe(topicAry, topicQos);
    }

    //在断开连接时调用
    @Override
    public void connectionLost(Throwable ex) {
        log.error("{}", ex);
    }

    //已经预订的消息
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        CloudEventObserverEntity observer = observerMap.get(topic);

        try {
            Event event = new Event(topic, new String(message.getPayload())).qos(message.getQos());
            observer.handler(event);
        } catch (Throwable ex) {
            log.error("{}", ex);
            throw new RuntimeException(ex);
        }
    }

    //发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
