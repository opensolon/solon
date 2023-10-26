package org.noear.solon.cloud.extend.mqtt5.service;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author noear
 * @since 2.4
 */
public class CloudEventServiceMqtt5 implements CloudEventServicePlus {
    private static final String PROP_EVENT_clientId = "event.clientId";

    private final CloudProps cloudProps;

    private final String server;
    private final String username;
    private final String password;
    private final long publishTimeout;

    private IMqttAsyncClient client;
    private String clientId;
    private MqttCallback clientCallback;
    private MqttConnectionOptions options;

    /**
     * 获取客户端
     * */
    public IMqttAsyncClient getClient() {
        return client;
    }

    //
    // 1833(MQTT的默认端口号)
    //
    public CloudEventServiceMqtt5(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        this.server = cloudProps.getEventServer();
        this.username = cloudProps.getUsername();
        this.password = cloudProps.getPassword();
        this.publishTimeout = cloudProps.getEventPublishTimeout();

        connect();
    }


    private synchronized void connect() {
        if (client != null) {
            return;
        }

        clientId = getEventClientId();
        if (Utils.isEmpty(clientId)) {
            clientId = Solon.cfg().appName() + "-" + Utils.guid();
        }

        options = new MqttConnectionOptions();

        if (Utils.isNotEmpty(username)) {
            options.setUserName(username);
        } else {
            options.setUserName(Solon.cfg().appName());
        }

        if (Utils.isNotEmpty(password)) {
            options.setPassword(password.getBytes(StandardCharsets.UTF_8));
        }

        options.setConnectionTimeout(30); //超时时长；秒
        options.setKeepAliveInterval(60); //心跳时长；秒
        options.setServerURIs(new String[]{server});
        options.setAutomaticReconnect(true);
        //options.setAutomaticReconnectDelay(10, 365*24*60*60);

        //绑定定制属性
        Properties props = cloudProps.getEventClientProps();
        if (props.size() > 0) {
            Utils.injectProperties(options, props);
        }

        //设置死信
        options.setWill("client.close", new MqttMessage(clientId.getBytes(StandardCharsets.UTF_8), 1, false, null));

        try {
            client = new MqttAsyncClient(server, clientId, new MemoryPersistence());
            clientCallback = new MqttCallbackImpl(client, observerMap, cloudProps);

            client.setCallback(clientCallback);
            //转为毫秒
            long waitConnectionTimeout = options.getConnectionTimeout() * 1000;
            client.connect(options).waitForCompletion(waitConnectionTimeout);
        } catch (MqttException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        MqttMessage message = new MqttMessage();
        message.setQos(event.qos());
        message.setRetained(event.retained());
        message.setPayload(event.content().getBytes());


        try {
            IMqttToken token = client.publish(event.topic(), message);

            if (event.qos() > 0) {
                token.waitForCompletion(publishTimeout);
                return token.isComplete();
            } else {
                return true;
            }
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
    }

    CloudEventObserverManger observerMap = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, int qos,CloudEventHandler observer) {
        observerMap.add(topic, level, group, topic, tag, qos, observer);
    }

    public void subscribe() {
        try {
            if (observerMap.topicSize() > 0) {
                MqttUtil.subscribe(client, options, cloudProps.getEventChannel(), observerMap);
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
            channel = cloudProps.getEventChannel();
        }
        return channel;
    }

    @Override
    public String getGroup() {
        if (group == null) {
            group = cloudProps.getEventGroup();
        }

        return group;
    }

    public String getEventClientId() {
        return cloudProps.getValue(PROP_EVENT_clientId);
    }
}
