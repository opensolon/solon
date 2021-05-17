package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;
import org.noear.solon.cloud.model.Event;

import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqProducer {
    RocketmqConfig cfg;
    long timeout;
    DefaultMQProducer producer;

    public RocketmqProducer(RocketmqConfig config) {
        cfg = config;
        timeout = RocketmqProps.instance.getEventPublishTimeout();
    }

    private void init() throws MQClientException {
        if (producer != null) {
            return;
        }

        synchronized (this) {
            if (producer != null) {
                return;
            }


            producer = new DefaultMQProducer(cfg.exchangeName);
            producer.setNamesrvAddr(cfg.server);
            //发送超时时间，默认3000 单位ms
            if (timeout > 0) {
                producer.setSendMsgTimeout((int)timeout);
            }
            //失败后重试2次
            producer.setRetryTimesWhenSendFailed(2);

            //绑定定制属性
            Properties props = RocketmqProps.instance.getEventProducerProps();
            if(props.size() > 0) {
                Utils.injectProperties(producer, props);
            }

            producer.start();
        }
    }

    public boolean publish(Event event) throws MQClientException, RemotingException, MQBrokerException, InterruptedException{
        init();

        Message message = MessageUtil.buildNewMeaage(event);

        SendResult send = producer.send(message);

        if (send.getSendStatus().equals(SendStatus.SEND_OK)) {
            return true;
        } else {
            return false;
        }
    }
}
