package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.model.Event;

import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqProducer {
    final RocketmqConfig config;
    DefaultMQProducer producer;

    public RocketmqProducer(RocketmqConfig config) {
        this.config = config;
    }

    private void lazyInit(CloudProps cloudProps) throws MQClientException {
        if (producer != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (producer != null) {
                return;
            }

            if (Utils.isEmpty(config.getAccessKey())) {
                producer = new DefaultMQProducer();
            } else {
                RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(config.getAccessKey(), config.getSecretKey()));
                producer = new DefaultMQProducer(rpcHook);
            }


            //服务地址
            producer.setNamesrvAddr(config.getServer());
            //生产组
            producer.setProducerGroup(config.getProducerGroup());
            //命名空间
            if (Utils.isNotEmpty(config.getNamespace())) {
                producer.setNamespace(config.getNamespace());
            }

            //发送超时时间，默认3000 单位ms
            if (config.getTimeout() > 0) {
                producer.setSendMsgTimeout((int) config.getTimeout());
            }
            //失败后重试2次
            producer.setRetryTimesWhenSendFailed(2);

            //绑定定制属性
            Properties props = cloudProps.getEventProducerProps();
            if (props.size() > 0) {
                Utils.injectProperties(producer, props);
            }

            producer.start();
        } finally {
            Utils.locker().unlock();
        }
    }

    public boolean publish(CloudProps cloudProps, Event event, String topic) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        lazyInit(cloudProps);

        Message message = MessageUtil.buildNewMeaage(event, topic);

        SendResult send = producer.send(message);

        if (send.getSendStatus().equals(SendStatus.SEND_OK)) {
            return true;
        } else {
            return false;
        }
    }
}
