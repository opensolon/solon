package org.noear.solon.cloud.extend.aliyun.rocketmq.impl;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cgy
 * @since 1.11.3
 */
public class RocketmqConsumer {
    private RocketmqConfig cfg;
    private ConsumerBean consumer;
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
            consumer = new ConsumerBean();
            consumer.setProperties(cfg.getConsumerProperties());

            Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>(observerManger.topicSize());
            for (String topic : observerManger.topicAll()) {
                Subscription subscription = new Subscription();
                subscription.setTopic(topic);
                subscription.setExpression("*");
                subscriptionTable.put(subscription, handler);
            }
            consumer.setSubscriptionTable(subscriptionTable);
            consumer.start();
        }
    }
}
