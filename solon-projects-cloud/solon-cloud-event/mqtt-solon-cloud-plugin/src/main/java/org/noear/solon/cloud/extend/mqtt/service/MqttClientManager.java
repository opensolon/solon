package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;

/**
 * Mqtt 客户端管理器（便于支持自动重连和触发式重连）
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
     * 设置异步状态
     */
    void setAsync(boolean async);

    /**
     * 获取异步状态
     */
    boolean getAsync();

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
