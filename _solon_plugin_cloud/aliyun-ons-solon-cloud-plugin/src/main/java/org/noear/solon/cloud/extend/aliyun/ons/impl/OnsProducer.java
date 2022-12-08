package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.shaded.org.apache.rocketmq.client.exception.ClientException;
import org.noear.solon.cloud.model.Event;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsProducer {
    OnsConfig cfg;
    Producer producer;

    public OnsProducer(OnsConfig config) {
        cfg = config;
    }

    private void init() throws ClientException {
        if (producer != null) {
            return;
        }
        synchronized (this) {
            if (producer != null) {
                return;
            }
            producer = ONSFactory.createProducer(cfg.getProducerProperties());
        }
    }

    public boolean publish(Event event, String topic) throws ClientException {
        init();
        //普通消息发送。
        Message message = MessageUtil.buildNewMessage(event, topic);
        //发送消息，需要关注发送结果，并捕获失败等异常。
        SendResult sendReceipt = producer.send(message);
        if (sendReceipt != null) {
            return true;
        } else {
            return false;
        }
    }
}
