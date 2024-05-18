package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.model.EventObserverMap;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsConsumer implements Closeable {
    static Logger log = LoggerFactory.getLogger(OnsConsumer.class);

    private final OnsConfig config;
    private ConsumerBean consumerOfCluster;
    private ConsumerBean consumerOfInstance;

    public OnsConsumer(OnsConfig config) {
        this.config = config;
    }

    public void init(CloudEventObserverManger observerManger) {
        if (consumerOfCluster != null) {
            return;
        }

        Utils.locker().lock();

        try {
            if (consumerOfCluster != null) {
                return;
            }

            consumerOfCluster = buildConsumer(observerManger, config.getConsumerGroup(), EventLevel.cluster);
            consumerOfInstance = buildConsumer(observerManger, Instance.local().serviceAndAddress(), EventLevel.instance);

        } finally {
            Utils.locker().unlock();
        }
    }

    private ConsumerBean buildConsumer(CloudEventObserverManger observerManger, String consumerGroup, EventLevel eventLevel) {
        ConsumerBean consumer = new ConsumerBean();

        consumer.setProperties(config.getConsumerProperties(consumerGroup));

        OnsConsumerHandler handler = new OnsConsumerHandler(config, observerManger);

        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();

        for (String topic : observerManger.topicAll()) {
            EventObserverMap tagsObserverMap = observerManger.topicOf(topic);
            Collection<String> tags = tagsObserverMap.getTagsByLevel(eventLevel);

            if (tags.size() > 0) {
                String tagsExpr = String.join("||", tags);

                Subscription subscription = new Subscription();
                subscription.setTopic(topic);
                if (tags.contains("*")) {
                    subscription.setExpression("*");
                } else {
                    subscription.setExpression(tagsExpr);
                }

                subscriptionTable.put(subscription, handler);

                log.trace("Ons consumer will subscribe [" + topic + "(" + tagsExpr + ")] ok!");
            }
        }

        consumer.setSubscriptionTable(subscriptionTable);
        consumer.start();

        if (consumer.isStarted()) {
            log.trace("Ons consumer started!");
        } else {
            log.warn("Ons consumer start failure!");
        }

        return consumer;
    }

    @Override
    public void close() throws IOException {
        if (consumerOfCluster != null) {
            consumerOfCluster.shutdown();
        }

        if (consumerOfInstance != null) {
            consumerOfInstance.shutdown();
        }
    }
}