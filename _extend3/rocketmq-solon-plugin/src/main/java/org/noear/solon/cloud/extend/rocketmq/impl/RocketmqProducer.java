package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.noear.solon.cloud.model.Event;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqProducer {
    RocketmqConfig cfg;

    DefaultMQProducer producer;

    public RocketmqProducer(RocketmqConfig config){
        cfg = config;
    }

    private void init(){
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
            producer.setSendMsgTimeout(3000);
            //失败后重试2次
            producer.setRetryTimesWhenSendFailed(2);

            try {
                producer.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public boolean publish(Event event) {
        init();

        try {
            Message message = MessageUtil.buildNewMeaage(event);

            SendResult send = producer.send(message);

            if (send.getSendStatus().equals(SendStatus.SEND_OK)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
