package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConsumer {
    private RocketmqConfig cfg;

    ClientServiceProvider serviceProvider;
    private PushConsumer consumer;
    private RocketmqConsumerHandler handler;

    public RocketmqConsumer(RocketmqConfig config) {
        cfg = config;
    }

    public void init(CloudProps cloudProps, CloudEventObserverManger observerManger) throws ClientException {
        if (consumer != null) {
            return;
        }

        synchronized (this) {
            if (consumer != null) {
                return;
            }

            handler = new RocketmqConsumerHandler(cloudProps, observerManger);

            serviceProvider = ClientServiceProvider.loadService();

            ClientConfigurationBuilder builder = ClientConfiguration.newBuilder();

            //服务地址
            builder.setEndpoints(cfg.getServer());

            //发送超时时间，默认3000 单位ms
            if (cfg.getTimeout() > 0) {
                builder.setRequestTimeout(Duration.ofMillis(cfg.getTimeout()));
            }

            ClientConfiguration configuration = builder.build();


            Map<String,FilterExpression> subscriptionExpressions = new LinkedHashMap<>();
            //要消费的topic，可使用tag进行简单过滤
            for (String topic : observerManger.topicAll()) {
                subscriptionExpressions.put(topic, FilterExpression.SUB_ALL);
            }

            PushConsumerBuilder consumerBuilder = serviceProvider.newPushConsumerBuilder()
                    .setClientConfiguration(configuration)
                    //消费组
                    .setConsumerGroup(cfg.getConsumerGroup())
                    //.setConsumptionThreadCount(1)
                    .setSubscriptionExpressions(subscriptionExpressions)
                    .setMessageListener(handler);

            consumer = consumerBuilder.build();
        }
    }
}
