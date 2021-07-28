package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.mqtt.MqttProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.cloud.service.CloudEventServicePlus;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventServiceMqttImp implements CloudEventServicePlus {
    private static CloudEventServiceMqttImp instance;

    public static synchronized CloudEventServiceMqttImp getInstance() {
        if (instance == null) {
            instance = new CloudEventServiceMqttImp();
        }

        return instance;
    }


    private final String server;
    private final String username;
    private final String password;

    private MqttClient client;
    private String clientId;
    private MqttCallbackImp clientCallback;

    //
    // 1833(MQTT的默认端口号)
    //
    private CloudEventServiceMqttImp() {
        this.server = MqttProps.instance.getEventServer();
        this.username = MqttProps.instance.getUsername();
        this.password = MqttProps.instance.getPassword();

        connect();
    }

    public CloudEventServiceMqttImp(Properties props) {
        this.server = props.getProperty("server");
        this.username = props.getProperty("username");
        this.password = props.getProperty("password");

        connect();
    }

    private synchronized void connect() {
        if (client != null) {
            return;
        }

        clientId = MqttProps.clientId();
        if (Utils.isEmpty(clientId)) {
            clientId = Solon.cfg().appName() + "-" + Utils.guid();
        }

        MqttConnectOptions options = new MqttConnectOptions();

        if (Utils.isNotEmpty(username)) {
            options.setUserName(username);
        } else {
            options.setUserName(Solon.cfg().appName());
        }

        if (Utils.isNotEmpty(password)) {
            options.setPassword(password.toCharArray());
        }

        options.setCleanSession(false);
        options.setConnectionTimeout(1000); //超时时长
        options.setKeepAliveInterval(100); //心跳时长
        options.setServerURIs(new String[]{server});

        //绑定定制属性
        Properties props = MqttProps.instance.getEventClientProps();
        if(props.size() > 0){
            Utils.injectProperties(options, props);
        }

        //设置死信
        options.setWill("client.close", clientId.getBytes(StandardCharsets.UTF_8), 1, false);

        try {
            client = new MqttClient(server, clientId, new MemoryPersistence());
            clientCallback = new MqttCallbackImp(client);

            client.setCallback(clientCallback);
            client.connect(options);
        } catch (MqttException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean publish(Event event) throws CloudEventException{
        MqttMessage message = new MqttMessage();
        message.setQos(event.qos());
        message.setRetained(event.retained());
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
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
    }

    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        try {
            if (observerMap.size() > 0) {
                clientCallback.subscribe(observerMap);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private String channel;
    private String group;

    @Override
    public String getChannel() {
        if (channel == null) {
            channel = MqttProps.instance.getEventChannel();
        }
        return channel;
    }

    @Override
    public String getGroup() {
        if (group == null) {
            group = MqttProps.instance.getEventGroup();
        }

        return group;
    }
}
