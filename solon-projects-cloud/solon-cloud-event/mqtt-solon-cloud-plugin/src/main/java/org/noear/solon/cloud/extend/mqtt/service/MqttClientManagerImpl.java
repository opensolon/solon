package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.EventObserver;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author noear
 * @since 2.5
 */
public class MqttClientManagerImpl implements MqttClientManager, MqttCallbackExtended {
    private static final Logger log = LoggerFactory.getLogger(MqttClientManagerImpl.class);

    private final String server;
    private final CloudEventObserverManger observerManger;
    private final String eventChannelName;

    private final MqttConnectOptions options;
    private String clientId;

    private final Set<ConnectCallback> connectCallbacks = Collections.synchronizedSet(new HashSet<>());


    public MqttClientManagerImpl(CloudEventObserverManger observerManger, CloudProps cloudProps, MqttConnectOptions options, String clientId) {
        this.observerManger = observerManger;
        this.eventChannelName = cloudProps.getEventChannel();
        this.server = cloudProps.getEventServer();
        this.options = options;
        this.clientId = clientId;
    }

    //在断开连接时调用
    @Override
    public synchronized void connectionLost(Throwable cause) {
        log.error("Connection lost, clientId={}", client.getClientId(), cause);

        if (options.isAutomaticReconnect()) {
            try {
                client.reconnect();
            } catch (MqttException e) {
                log.error("MQTT client failed to connect. Never happens.", e);
                client = null;
            }
        }
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

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        connectCallbacks.forEach(callback -> callback.connectComplete(reconnect));
    }


    IMqttAsyncClient client;

    /**
     * 获取客户端
     * */
    @Override
    public synchronized IMqttAsyncClient getClient() {
        if (client == null) {
            client = createClient();
        }

        return client;
    }

    @Override
    public void addCallback(ConnectCallback connectCallback) {
        connectCallbacks.add(connectCallback);
    }

    @Override
    public boolean removeCallback(ConnectCallback connectCallback) {
        return connectCallbacks.remove(connectCallback);
    }

    /**
     * 创建客户端
     */
    private IMqttAsyncClient createClient() {
        try {
            client = new MqttAsyncClient(server, clientId, new MemoryPersistence());

            client.setCallback(this);
            //转为毫秒
            long waitConnectionTimeout = options.getConnectionTimeout() * 1000;
            client.connect(options).waitForCompletion(waitConnectionTimeout);

            subscribe();

            return client;
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 订阅
     */
    private void subscribe() throws MqttException {
        if (observerManger.topicSize() < 1) {
            return;
        }

        String[] topicAry = observerManger.topicAll().toArray(new String[0]);
        int[] topicQos = new int[topicAry.length];
        IMqttMessageListener[] topicListener = new IMqttMessageListener[topicAry.length];
        for (int i = 0, len = topicQos.length; i < len; i++) {
            EventObserver eventObserver = observerManger.getByTopic(topicAry[i]);
            topicQos[i] = eventObserver.getQos();
            topicListener[i] = new MqttMessageListenerImpl(eventChannelName, eventObserver);
        }

        IMqttToken token = getClient().subscribe(topicAry, topicQos, topicListener);
        //转为毫秒
        long waitConnectionTimeout = options.getConnectionTimeout() * 1000;
        token.waitForCompletion(waitConnectionTimeout);
    }
}
