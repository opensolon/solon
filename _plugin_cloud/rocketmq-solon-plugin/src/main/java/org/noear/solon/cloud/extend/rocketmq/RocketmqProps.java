package org.noear.solon.cloud.extend.rocketmq;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RocketmqProps {
    public static final String GROUP_SPLIT_MART = "--";

    static final String EVENT_CONSUMER_GROUP = "solon.cloud.rocketmq.event.consumerGroup";
    static final String EVENT_PRODUCER_GROUP = "solon.cloud.rocketmq.event.producerGroup";
    static final String EVENT_NAMESPACE = "solon.cloud.rocketmq.event.namespace";

    public static final CloudProps instance = new CloudProps("rocketmq");

    /**
     * 命名空间
     * */
    public static String getEventNamespace() {
        return Solon.cfg().get(EVENT_NAMESPACE);
    }

    /**
     * 消费组
     * */
    public static String getEventConsumerGroup() {
        return Solon.cfg().get(EVENT_CONSUMER_GROUP);
    }

    /**
     * 产品组
     * */
    public static String getEventProducerGroup() {
        return Solon.cfg().get(EVENT_PRODUCER_GROUP);
    }
}
