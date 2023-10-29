package org.noear.solon.cloud.extend.mqtt5.service;


import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;

/**
 * Mqtt 客户端管理器（可以校好的支持自动重连和触发式重链）
 *
 * @author noear
 * @since 2.5
 */
public interface MqttClientManager {
    /**
     * 获取客户端
     */
    IMqttAsyncClient getClient();

    /**
     * 获取客户端Id
     */
    String getClientId();

    /**
     * 添加连接回调
     */
    void addCallback(ConnectCallback connectCallback);

    /**
     * 移除连接回调
     */
    boolean removeCallback(ConnectCallback connectCallback);

    @FunctionalInterface
    public interface ConnectCallback {
        void connectComplete(boolean isReconnect);
    }
}
