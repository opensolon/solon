package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.mqtt.MqttProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventServiceImp implements CloudEventService {
    private final String server;
    private final String username;
    private final String password;

    private MqttClient client;
    private MqttCallbackImp clientCallback;

    //
    // 1833(MQTT的默认端口号)
    //
    public CloudEventServiceImp(String server) {
        this.server = server;
        this.username = MqttProps.instance.getUsername();
        this.password = MqttProps.instance.getPassword();
    }

    private synchronized void connect() throws MqttException {
        if (client != null) {
            return;
        }

        client = new MqttClient(server, Solon.cfg().appName(), new MemoryPersistence());
        clientCallback = new MqttCallbackImp(client);

        MqttConnectOptions options = new MqttConnectOptions();

        if (Utils.isNotEmpty(username)) {
            options.setUserName(username);
        }

        if (Utils.isNotEmpty(password)) {
            options.setPassword(password.toCharArray());
        }

        options.setCleanSession(false);
        options.setConnectionTimeout(1000); //超时时长
        options.setKeepAliveInterval(100); //心跳时长
        options.setServerURIs(new String[]{MqttProps.instance.getEventServer()});

        client.setCallback(clientCallback);
        client.connect(options);
    }

    @Override
    public boolean publish(Event event) {
        MqttMessage message = new MqttMessage();
        message.setQos(event.qos());
        message.setRetained(true);
        message.setPayload(event.content().getBytes());

        MqttTopic mqttTopic = client.getTopic(event.topic());

        try {
            MqttDeliveryToken token = mqttTopic.publish(message);

            if (event.qos() > 0) {
                token.waitForCompletion(1000 * 30);
                return token.isComplete();
            } else {
                return true;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        try {
            clientCallback.subscribe(observerMap);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
