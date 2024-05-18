package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.RPCHook;
import org.noear.solon.Utils;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.EventObserver;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author noear
 * @since 1.3
 * @since 1.11
 */
public class RocketmqConsumer implements Closeable {
    static Logger log = LoggerFactory.getLogger(RocketmqConsumer.class);

    private RocketmqConfig config;

    private DefaultMQPushConsumer consumerOfCluster;
    private DefaultMQPushConsumer consumerOfInstance;

    public RocketmqConsumer(RocketmqConfig config) {
        this.config = config;
    }

    public void init(CloudEventObserverManger observerManger) throws MQClientException {
        if (consumerOfCluster != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (consumerOfCluster != null) {
                return;
            }

            consumerOfCluster = buildConsumer(observerManger, config.getConsumerGroup(), EventLevel.cluster);
            consumerOfInstance = buildConsumer(observerManger, Instance.local().serviceAndAddress(), EventLevel.instance);

            log.trace("Rocketmq consumer started!");
        } finally {
            Utils.locker().unlock();
        }
    }

    private DefaultMQPushConsumer buildConsumer(CloudEventObserverManger observerManger, String consumerGroup, EventLevel eventLevel) throws MQClientException {
        DefaultMQPushConsumer consumer;

        if (Utils.isEmpty(config.getAccessKey())) {
            consumer = new DefaultMQPushConsumer();
        } else {
            RPCHook rpcHook = new AclClientRPCHook(new SessionCredentials(config.getAccessKey(), config.getSecretKey()));
            consumer = new DefaultMQPushConsumer(rpcHook);
        }


        //服务地址
        consumer.setNamesrvAddr(config.getServer());
        //消费组
        consumer.setConsumerGroup(consumerGroup);
        //命名空间
        if (Utils.isNotEmpty(config.getNamespace())) {
            consumer.setNamespace(config.getNamespace());
        }

        //消息线程数
        if (config.getConsumeThreadNums() > 0) {
            consumer.setConsumeThreadMax(config.getConsumeThreadNums());
        }

        //最大尝试次数（之后进死信）
        if (config.getMaxReconsumeTimes() > 0) {
            consumer.setMaxReconsumeTimes(config.getMaxReconsumeTimes());
        }

        //一次最大消费的条数
        consumerOfCluster.setConsumeMessageBatchMaxSize(1); //1是默认值
        //一次最大拉取的条数
        consumerOfCluster.setPullBatchSize(32); //32是默认值
        //无消息时，最大阻塞时间。默认5000 单位ms

        //绑定定制属性
        Properties props = config.getCloudProps().getEventConsumerProps();
        if (props.size() > 0) {
            Utils.injectProperties(consumerOfCluster, props);
        }

        boolean isOk = false;

        //要消费的topic，可使用tag进行简单过滤
        for (Map.Entry<String, Set<String>> kv : observerManger.topicTags().entrySet()) {
            EventObserver observer = observerManger.getByTopic(kv.getKey());

            if (observer.getLevel() == eventLevel) {
                String topic = kv.getKey();
                Set<String> tags = kv.getValue();
                String tagsExpr = String.join("||", tags);

                //支持 tag 过滤
                if (tags.contains("*")) {
                    consumer.subscribe(topic, "*");
                } else {
                    consumer.subscribe(topic, tagsExpr);
                }

                isOk = true;
                log.trace("Rocketmq consumer subscribe [" + topic + "(" + tagsExpr + ")] ok!");
            }
        }

        if (isOk == false) {
            return null;
        }

        consumer.registerMessageListener(new RocketmqConsumerHandler(config, observerManger));
        consumer.start();

        return consumer;
    }

    @Override
    public void close() throws IOException {
        if (consumerOfCluster != null) {
            consumerOfCluster.shutdown();
        }

        if (consumerOfInstance != null) {
            consumerOfInstance.shutdown();
        }
    }
}
