package org.noear.solon.cloud.extend.mqtt5.service;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.core.util.RunUtil;

/**
 * 消息监听（与主题一对一）
 *
 * @author noear
 * @since 2.4
 */
public class MqttMessageListenerImpl implements IMqttMessageListener {
    private CloudEventHandler eventHandler;
    private String eventChannelName;
    private MqttClientManager clientManager;

    public MqttMessageListenerImpl(MqttClientManager clientManager, String eventChannelName, CloudEventHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.eventChannelName = eventChannelName;
        this.clientManager = clientManager;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        MqttMessageHandler handler = new MqttMessageHandler(clientManager, eventChannelName, eventHandler, topic, message);

        if (clientManager.getAsync()) {
            RunUtil.parallel(handler);
        } else {
            handler.run();
        }
    }
}
