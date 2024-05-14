package org.noear.solon.cloud.extend.mqtt5.service;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.model.EventTran;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 * @since 2.5
 */
public class CloudEventServiceMqtt5 implements CloudEventServicePlus {
    private static final Logger log = LoggerFactory.getLogger(CloudEventServiceMqtt5.class);

    private final CloudProps cloudProps;

    private final long publishTimeout;

    private MqttClientManagerImpl clientManager;

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
    public CloudEventServiceMqtt5(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        this.publishTimeout = cloudProps.getEventPublishTimeout();

        this.clientManager = new MqttClientManagerImpl(observerMap, cloudProps);
    }

    private void beginTransaction(EventTran transaction) throws CloudEventException {
        //不支持事务消息
        log.warn("Event transactions are not supported!");
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        if(event.tran() != null){
            beginTransaction(event.tran());
        }

        MqttMessage message = new MqttMessage();
        message.setQos(event.qos());
        message.setRetained(event.retained());
        message.setPayload(event.content().getBytes());

        try {
            IMqttToken token = clientManager.getClient().publish(event.topic(), message);

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
}
