package org.noear.solon.cloud.extend.mqtt.impl;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * @author noear 2020/12/19 created
 */
public class Publisher {
    //tcp://MQTT安装的服务器地址:MQTT定义的端口号

    public static final String HOST = "tcp://localhost:51883";

    //定义一个主题

    public static final String TOPIC = "speedTopic";

    //定义MQTT的ID，可以在MQTT服务配置中指定

    private static final String clientid = "server84";

    private MqttClient client;

    private MqttTopic topic;

    private String userName = "mosquitto";

    private String password = "";

    private MqttMessage message;

    /**
     * 构造函数
     *
     * @throws MqttException
     */

    public Publisher() throws MqttException {

// MemoryPersistence设置clientid的保存形式，默认为以内存保存

        client = new MqttClient(HOST, clientid, new MemoryPersistence());

        connect();

    }

    private void connect() {

        MqttConnectOptions options = new MqttConnectOptions();

        options.setCleanSession(false);

//    options.setUserName(userName);

//    options.setPassword(password.toCharArray());

        //超时时长

        options.setConnectionTimeout(100);

        //心跳时长

        options.setKeepAliveInterval(20);

        options.setServerURIs(new String[]{HOST});

        try {

            client.setCallback(new PushCallback());

            client.connect(options);

            topic = client.getTopic(TOPIC);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void publish(MqttTopic topic, MqttMessage message) throws MqttException {

        MqttDeliveryToken token = topic.publish(message);

        System.out.println("等待发送成功:" + token.isComplete());

        token.waitForCompletion();

        System.out.println("已经发送成功:" + token.isComplete());

    }

    public static void main(String[] args) throws MqttException {

        Publisher server = new Publisher();

        server.message = new MqttMessage();

        server.message.setQos(1);

        server.message.setRetained(true);

        for (int i = 0; i < 10; i++) {

            server.message.setPayload(("hello,topic speed " + i).getBytes());

            server.publish(server.topic, server.message);

        }

    }
}
