package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

/**
 * @author noear
 * @since 2.0
 */
public interface MqttClientManager {
    IMqttAsyncClient getClient();

    void addCallback(ConnectCallback connectCallback);

    boolean removeCallback(ConnectCallback connectCallback);

    @FunctionalInterface
    public interface ConnectCallback {
        void connectComplete(boolean isReconnect);
    }
}
