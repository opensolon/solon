package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;
import org.noear.solon.cloud.service.CloudEventObserverEntity;

import java.util.Map;
import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConsumer {
    RocketmqConfig cfg;

    DefaultMQPushConsumer consumer;
    RocketmqConsumerHandler handler;

    Map<String, CloudEventObserverEntity> observerMap;

    public RocketmqConsumer(RocketmqConfig config){
        cfg = config;
    }

    public void init(Map<String, CloudEventObserverEntity> observers){
        if (consumer != null) {
            return;
        }

        synchronized (this) {
            if (consumer != null) {
                return;
            }

            observerMap = observers;
            handler = new RocketmqConsumerHandler(observerMap);

            consumer = new DefaultMQPushConsumer();

            //服务地址
            consumer.setNamesrvAddr(cfg.server);
            //消费组
            consumer.setConsumerGroup(cfg.consumerGroup);
            //命名空间
            if(Utils.isNotEmpty(cfg.namespace)) {
                consumer.setNamespace(cfg.namespace);
            }


            //一次最大消费的条数
            consumer.setConsumeMessageBatchMaxSize(1); //1是默认值
            //一次最大拉取的条数
            consumer.setPullBatchSize(32); //32是默认值
            //无消息时，最大阻塞时间。默认5000 单位ms

            //绑定定制属性
            Properties props = RocketmqProps.instance.getEventConsumerProps();
            if(props.size() > 0) {
                Utils.injectProperties(consumer, props);
            }

            try {
                //要消费的topic，可使用tag进行简单过滤
                for (String topic : observerMap.keySet()) {
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
