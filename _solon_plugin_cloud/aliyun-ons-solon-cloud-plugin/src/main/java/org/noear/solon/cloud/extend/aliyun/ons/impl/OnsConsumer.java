package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsConsumer {
    private OnsConfig cfg;
    private ConsumerBean consumer;
    private OnsConsumerHandler handler;

    public OnsConsumer(OnsConfig config) {
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

            handler = new OnsConsumerHandler(cfg, cloudProps, observerManger);
            consumer = new ConsumerBean();
            consumer.setProperties(cfg.getConsumerProperties());

            Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>(observerManger.topicSize());
            for (String topic : observerManger.topicAll()) {
                Subscription subscription = new Subscription();
                subscription.setTopic(topic);
                subscription.setExpression("*");
                subscriptionTable.put(subscription, handler);
                if (cfg.getEnableConsoleLog()) {
                    LogUtil.global().info("rocketMq onsConsumer subscribe [" + topic + "] ok!");
                }
            }
            consumer.setSubscriptionTable(subscriptionTable);
            consumer.start();
            if (cfg.getEnableConsoleLog()) {
                LogUtil.global().info("rocketMq onsConsumer [" + consumer.isStarted() + "]!");
            }
        }
    }
}
