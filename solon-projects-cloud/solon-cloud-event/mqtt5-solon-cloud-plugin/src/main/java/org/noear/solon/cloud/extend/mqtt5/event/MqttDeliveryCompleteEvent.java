package org.noear.solon.cloud.extend.mqtt5.event;

import org.eclipse.paho.mqttv5.client.IMqttToken;

import java.io.Serializable;

/**
 * @author noear
 * @since 2.5
 */
public class MqttDeliveryCompleteEvent implements Serializable {
    private String clientId;
    private int messageId;
    private transient IMqttToken token;
    public MqttDeliveryCompleteEvent(String clientId, int messageId, IMqttToken token){
        this.clientId = clientId;
        this.messageId = messageId;
        this.token = token;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getClientId() {
        return clientId;
    }

    public IMqttToken getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "MqttDeliveryCompleteEvent{" +
                "clientId='" + clientId + '\'' +
                ", messageId=" + messageId +
                '}';
    }
}
