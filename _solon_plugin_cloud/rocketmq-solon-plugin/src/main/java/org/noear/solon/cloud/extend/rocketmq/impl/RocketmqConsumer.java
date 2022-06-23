package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;

import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConsumer {
    private RocketmqConfig cfg;

    private DefaultMQPushConsumer consumer;
    private RocketmqConsumerHandler handler;

    public RocketmqConsumer(RocketmqConfig config) {
        cfg = config;
    }

    public void init(CloudProps cloudProps, CloudEventObserverManger observerManger) {
        if (consumer != null) {
            return;
        }

        synchronized (this) {
            if (consumer != null) {
                return;
            }

            handler = new RocketmqConsumerHandler(cloudProps, observerManger);

            consumer = new DefaultMQPushConsumer();

            //服务地址
            consumer.setNamesrvAddr(cfg.getServer());
            //消费组
            consumer.setConsumerGroup(cfg.getConsumerGroup());
            //命名空间
            if (Utils.isNotEmpty(cfg.getNamespace())) {
                consumer.setNamespace(cfg.getNamespace());
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

            try {
                //要消费的topic，可使用tag进行简单过滤
                for (String topic : observerManger.topicAll()) {
                    consumer.subscribe(topic, "*");
                }

                consumer.registerMessageListener(handler);
                consumer.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
