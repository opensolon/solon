package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsProducer implements Closeable {
    static Logger log = LoggerFactory.getLogger(OnsProducer.class);

    private OnsConfig config;
    private Producer producer;

    public OnsProducer(OnsConfig config) {
        this.config = config;
    }

    private void init() {
        if (producer != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (producer != null) {
                return;
            }
            producer = ONSFactory.createProducer(config.getProducerProperties());
            producer.start();

            log.debug("Ons producer started: " + producer.isStarted());

        } finally {
            Utils.locker().unlock();
        }
    }

    public boolean publish(Event event, String topic) {
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

    @Override
    public void close() throws IOException {

    }
}
