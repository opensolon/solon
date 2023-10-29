package org.noear.solon.cloud.extend.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.noear.solon.cloud.CloudEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主题消息监听（与主题一对一）
 *
 * @author noear
 * @since 2.4
 */
public class MqttMessageListenerImpl implements IMqttMessageListener {
    static Logger log = LoggerFactory.getLogger(MqttMessageListenerImpl.class);

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
        MqttUtil.receive(clientManager, log, eventChannelName, eventHandler, topic, message);
    }
}
