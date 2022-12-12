package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author noear
 * @since 1.3
 * @since 1.11
 */
public class RocketmqConsumer {
    private RocketmqConfig config;

    private DefaultMQPushConsumer consumer;

    public RocketmqConsumer(RocketmqConfig config) {
        this.config = config;
    }

    public void init(CloudProps cloudProps, CloudEventObserverManger observerManger) throws MQClientException {
        if (consumer != null) {
            return;
        }

        synchronized (this) {
            if (consumer != null) {
                return;
            }

            RocketmqConsumerHandler handler = new RocketmqConsumerHandler(config, observerManger);

            consumer = new DefaultMQPushConsumer();

            //服务地址
            consumer.setNamesrvAddr(config.getServer());
            //消费组
            consumer.setConsumerGroup(config.getConsumerGroup());
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
            consumer.setConsumeMessageBatchMaxSize(1); //1是默认值
            //一次最大拉取的条数
            consumer.setPullBatchSize(32); //32是默认值
            //无消息时，最大阻塞时间。默认5000 单位ms

            //绑定定制属性
            Properties props = cloudProps.getEventConsumerProps();
            if (props.size() > 0) {
                Utils.injectProperties(consumer, props);
            }


            //要消费的topic，可使用tag进行简单过滤
            for (Map.Entry<String, Set<String>> kv : observerManger.topicTags().entrySet()) {
                String topic = kv.getKey();
                Set<String> tags = kv.getValue();

                //支持 tag 过滤
                if (tags.contains("*")) {
                    consumer.subscribe(topic, "*");
                } else {
                    consumer.subscribe(topic, String.join("||", tags));
                }
            }

            consumer.registerMessageListener(handler);
            consumer.start();
        }
    }
}
