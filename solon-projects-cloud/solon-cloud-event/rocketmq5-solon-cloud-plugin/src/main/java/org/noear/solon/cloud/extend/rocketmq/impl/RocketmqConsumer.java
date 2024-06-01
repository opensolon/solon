package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.noear.solon.Utils;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.EventObserverMap;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.3
 * @since 1.11
 */
public class RocketmqConsumer implements Closeable {
    static Logger log = LoggerFactory.getLogger(RocketmqConsumer.class);

    private RocketmqConfig config;

    private ClientServiceProvider serviceProvider;
    private PushConsumer consumerOfCluster;
    private PushConsumer consumerOfInstance;

    public RocketmqConsumer(RocketmqConfig config) {
        this.config = config;
        this.serviceProvider = ClientServiceProvider.loadService();
    }

    public void init(CloudEventObserverManger observerManger) throws ClientException {
        if (consumerOfCluster != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (consumerOfCluster != null) {
                return;
            }

            String instanceName = Instance.local().serviceAndAddress()
                    .replace("@","-")
                    .replace(".","_")
                    .replace(":","_");

            consumerOfCluster = buildConsumer(observerManger, config.getConsumerGroup(), EventLevel.cluster);
            consumerOfInstance = buildConsumer(observerManger, instanceName, EventLevel.instance);

            log.trace("Rocketmq5 consumer started!");
        } finally {
            Utils.locker().unlock();
        }
    }

    /**
     * @since 2.8
     * */
    private PushConsumer buildConsumer(CloudEventObserverManger observerManger, String consumerGroup, EventLevel eventLevel) throws ClientException {
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder();

        //服务地址
        builder.setEndpoints(config.getServer());
        //账号密码
        if (Utils.isNotEmpty(config.getAccessKey())) {
            builder.setCredentialProvider(new StaticSessionCredentialsProvider(config.getAccessKey(), config.getSecretKey()));
        }
        //发送超时时间，默认3000 单位ms
        if (config.getTimeout() > 0) {
            builder.setRequestTimeout(Duration.ofMillis(config.getTimeout()));
        }

        ClientConfiguration configuration = builder.build();


        Map<String, FilterExpression> subscriptionExpressions = new LinkedHashMap<>();
        //要消费的topic，可使用tag进行简单过滤
        for (String topic : observerManger.topicAll()) {
            EventObserverMap tagsObserverMap = observerManger.topicOf(topic);
            Collection<String> tags = tagsObserverMap.getTagsByLevel(eventLevel);

            if (tags.size() > 0) {
                String tagsExpr = String.join("||", tags);

                //支持 tag 过滤
                if (tags.contains("*")) {
                    subscriptionExpressions.put(topic, FilterExpression.SUB_ALL);
                } else {
                    subscriptionExpressions.put(topic, new FilterExpression(tagsExpr));
                }

                log.trace("Rocketmq5 consumer will subscribe [" + topic + "(" + tagsExpr + ")] ok!");
            }
        }

        if (subscriptionExpressions.size() == 0) {
            return null;
        }

        PushConsumerBuilder consumerBuilder = serviceProvider.newPushConsumerBuilder();

        consumerBuilder.setClientConfiguration(configuration);
        //消费组
        consumerBuilder.setConsumerGroup(consumerGroup);
        //监听
        consumerBuilder.setMessageListener(new RocketmqConsumerHandler(config, observerManger));
        //订阅
        if (subscriptionExpressions.size() > 0) {
            consumerBuilder.setSubscriptionExpressions(subscriptionExpressions);
        }
        //消费线程数
        if (config.getConsumeThreadNums() > 0) {
            consumerBuilder.setConsumptionThreadCount(config.getConsumeThreadNums());
        }

        return consumerBuilder.build();
    }

    @Override
    public void close() throws IOException {
        if (consumerOfCluster != null) {
            consumerOfCluster.close();
        }

        if (consumerOfInstance != null) {
            consumerOfInstance.close();
        }
    }
}
