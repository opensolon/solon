package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsConsumer {
    static Logger log = LoggerFactory.getLogger(OnsConsumer.class);

    private OnsConfig config;
    private ConsumerBean consumer;

    public OnsConsumer(OnsConfig config) {
        this.config = config;
    }

    public void init(CloudProps cloudProps, CloudEventObserverManger observerManger) {
        if (consumer != null) {
            return;
        }

        synchronized (this) {
            if (consumer != null) {
                return;
            }

            consumer = new ConsumerBean();
            consumer.setProperties(config.getConsumerProperties());

            OnsConsumerHandler handler = new OnsConsumerHandler(config, observerManger);

            Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
            for (Map.Entry<String, Set<String>> kv : observerManger.topicTags().entrySet()) {
                String topic = kv.getKey();
                Set<String> tags = kv.getValue();
                String tagsExpr = String.join("||", tags);

                Subscription subscription = new Subscription();
                subscription.setTopic(topic);
                if (tags.contains("*")) {
                    subscription.setExpression("*");
                } else {
                    subscription.setExpression(tagsExpr);
                }

                subscriptionTable.put(subscription, handler);

                log.trace("Ons consumer subscribe [" + topic + "(" + tagsExpr + ")] ok!");
            }

            consumer.setSubscriptionTable(subscriptionTable);
            consumer.start();

            if(consumer.isStarted()) {
                log.trace("Ons consumer started!");
            }else{
                log.warn("Ons consumer start failure!");
            }
        }
    }
}
