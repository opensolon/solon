package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 1.3
 * @since 1.11
 */
public class RocketmqConsumer implements Closeable {
    private RocketmqConfig config;

    ClientServiceProvider serviceProvider;
    private PushConsumer consumer;
    private RocketmqConsumerHandler handler;

    public RocketmqConsumer(RocketmqConfig config) {
        this.config = config;
    }

    public void init(CloudProps cloudProps, CloudEventObserverManger observerManger) throws ClientException {
        if (consumer != null) {
            return;
        }

        synchronized (this) {
            if (consumer != null) {
                return;
            }

            serviceProvider = ClientServiceProvider.loadService();

            ClientConfigurationBuilder builder = ClientConfiguration.newBuilder();

            //服务地址
            builder.setEndpoints(config.getServer());
            //账号密码
            if(Utils.isNotEmpty(config.getAccessKey())) {
                builder.setCredentialProvider(new StaticSessionCredentialsProvider(config.getAccessKey(), config.getSecretKey()));
            }

            //发送超时时间，默认3000 单位ms
            if (config.getTimeout() > 0) {
                builder.setRequestTimeout(Duration.ofMillis(config.getTimeout()));
            }

            ClientConfiguration configuration = builder.build();


            Map<String, FilterExpression> subscriptionExpressions = new LinkedHashMap<>();
            //要消费的topic，可使用tag进行简单过滤
            for (Map.Entry<String, Set<String>> kv : observerManger.topicTags().entrySet()) {
                String topic = kv.getKey();
                Set<String> tags = kv.getValue();

                //支持 tag 过滤
                if (tags.contains("*")) {
                    subscriptionExpressions.put(topic, FilterExpression.SUB_ALL);
                } else {
                    subscriptionExpressions.put(topic, new FilterExpression(String.join("||", tags)));
                }
            }

            PushConsumerBuilder consumerBuilder = serviceProvider.newPushConsumerBuilder();

            consumerBuilder.setClientConfiguration(configuration);
            //消费组
            consumerBuilder.setConsumerGroup(config.getConsumerGroup());
            //监听
            handler = new RocketmqConsumerHandler(config, observerManger);
            consumerBuilder.setMessageListener(handler);
            //订阅
            if (subscriptionExpressions.size() > 0) {
                consumerBuilder.setSubscriptionExpressions(subscriptionExpressions);
            }

            if (config.getConsumeThreadNums() > 0) {
                consumerBuilder.setConsumptionThreadCount(config.getConsumeThreadNums());
            }

            consumer = consumerBuilder.build();
        }
    }

    @Override
    public void close() throws IOException {
        if(consumer != null){
            consumer.close();
        }
    }
}
