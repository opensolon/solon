package org.noear.solon.cloud.extend.mqtt.impl;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.ScheduledExecutorService;


/**
 * @author noear 2020/12/19 created
 */
public class Subscriber {
    public static final String HOST ="tcp://localhost:51883";

    public static final String TOPIC ="speedTopic";

    private static final String clientid ="client83";

    private MqttClient  client;

    private MqttConnectOptions options;

//  private String userName = "admin";

//  private String passWord = "password";

    private ScheduledExecutorService scheduler;

    public static void main(String[] args) {

        Subscriber client = new Subscriber();

        client.start();

    }

    private void start() {

        try {

            client = new MqttClient(HOST, clientid, new MemoryPersistence());

            options = new MqttConnectOptions();

            options.setCleanSession(true);

            options.setConnectionTimeout(10);

            options.setKeepAliveInterval(20);

            client.setCallback(new PushCallback());

            MqttTopic topic = client.getTopic(TOPIC);

            //遗言

            options.setWill(topic, "close".getBytes(), 2, true);

            client.connect(options);

            int[] qos = {1};

            String[] topics = {TOPIC};

            client.subscribe(topics, qos);

        } catch (MqttException e) {

            e.printStackTrace();

        }

    }
}
