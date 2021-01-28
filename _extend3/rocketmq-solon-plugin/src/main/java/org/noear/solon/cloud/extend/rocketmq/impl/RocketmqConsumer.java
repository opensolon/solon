package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.service.CloudEventObserverEntity;

import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConsumer {
    String server;
    String group;

    DefaultMQPushConsumer consumer;
    RocketmqConsumerHandler handler;

    Map<String, CloudEventObserverEntity> observerMap;

    public RocketmqConsumer(){
        this.server = server;
        this.group = Solon.cfg().appGroup();

        if (Utils.isEmpty(group)) {
            group = "DEFAULT_GROUP";
        }
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

            consumer = new DefaultMQPushConsumer(group);

            consumer.setNamesrvAddr(server);
            //一次最大消费的条数
            consumer.setPullBatchSize(1);
            //无消息时，最大阻塞时间。默认5000 单位ms
            try {
                //要消费的topic，可使用tag进行简单过滤
                for (String topic : observerMap.keySet()) {
                    consumer.subscribe(topic, "*");
                }

                consumer.registerMessageListener(handler);
                consumer.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
}
