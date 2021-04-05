package org.noear.solon.cloud.extend.mqtt.impl;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.time.LocalDateTime;

/**
 * @author noear 2020/12/19 created
 */
public class PushCallback implements MqttCallback {
    //在断开连接时调用
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("连接断开！");

        System.out.println(LocalDateTime.now());
    }

    //接收已经预订的发布。
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("接收消息主题 : " + topic);

        System.out.println("接收消息Qos : " + message.getQos());

        System.out.println("接收消息内容 : " + new String(message.getPayload()));

    }

    //接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("分发完成---------" + token.isComplete());

        System.out.println(LocalDateTime.now());

    }
}
