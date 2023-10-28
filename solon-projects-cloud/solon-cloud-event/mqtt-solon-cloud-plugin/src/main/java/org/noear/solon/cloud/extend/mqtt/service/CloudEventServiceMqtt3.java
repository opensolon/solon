package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.*;
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
 * @since 1.3
 */
public class CloudEventServiceMqtt3 implements CloudEventServicePlus {
    private static final String PROP_EVENT_clientId = "event.clientId";

    private final CloudProps cloudProps;

    private final String server;
    private final String username;
    private final String password;
    private final long publishTimeout;

    private String clientId;
    private MqttClientManagerImpl clientManager;
    private MqttConnectOptions options;

    private CloudEventObserverManger observerMap = new CloudEventObserverManger();

    /**
     * 获取客户端
     */
    public MqttClientManager getClientManager() {
        return clientManager;
    }

    //
    // 1833(MQTT的默认端口号)
    //
    public CloudEventServiceMqtt3(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        this.server = cloudProps.getEventServer();
        this.username = cloudProps.getUsername();
        this.password = cloudProps.getPassword();
        this.publishTimeout = cloudProps.getEventPublishTimeout();

        this.clientId = getEventClientId();
        if (Utils.isEmpty(this.clientId)) {
            this.clientId = Solon.cfg().appName() + "-" + Utils.guid();
        }

        initClientManager();
    }


    private void initClientManager() {
        if (clientManager != null) {
            return;
        }

        options = new MqttConnectOptions();

        if (Utils.isNotEmpty(username)) {
            options.setUserName(username);
        } else {
            options.setUserName(Solon.cfg().appName());
        }

        if (Utils.isNotEmpty(password)) {
            options.setPassword(password.toCharArray());
        }

        options.setCleanSession(true);
        options.setConnectionTimeout(30); //超时时长
        options.setKeepAliveInterval(60); //心跳时长，秒
        options.setServerURIs(new String[]{server});
        options.setAutomaticReconnect(true);

        //绑定定制属性
        Properties props = cloudProps.getEventClientProps();
        if (props.size() > 0) {
            Utils.injectProperties(options, props);
        }

        //设置死信
        options.setWill("client.close", clientId.getBytes(StandardCharsets.UTF_8), 1, false);


        clientManager = new MqttClientManagerImpl(observerMap, cloudProps, options, clientId);

    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        MqttMessage message = new MqttMessage();
        message.setQos(event.qos());
        message.setRetained(event.retained());
        message.setPayload(event.content().getBytes());

        try {
            IMqttDeliveryToken token = clientManager.getClient().publish(event.topic(), message);

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


    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, int qos, CloudEventHandler observer) {
        observerMap.add(topic, level, group, topic, tag, qos, observer);
    }

    public void subscribe() {
        try {
            //获取客户端时，自动会完成订阅
            clientManager.getClient();
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
