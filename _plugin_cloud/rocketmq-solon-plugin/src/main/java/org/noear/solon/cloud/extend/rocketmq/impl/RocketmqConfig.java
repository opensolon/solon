package org.noear.solon.cloud.extend.rocketmq.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConfig {
    /**
     * 生产组
     */
    protected String producerGroup;

    /**
     * 消费组
     */
    protected String consumerGroup;

    protected String server;

    protected String namespace;

    protected long timeout;

    public RocketmqConfig() {
        server = RocketmqProps.instance.getEventServer();

        timeout = RocketmqProps.instance.getEventPublishTimeout();

        namespace = RocketmqProps.getEventNamespace();
        producerGroup = RocketmqProps.getEventProducerGroup();
        consumerGroup = RocketmqProps.getEventConsumerGroup();

        if (Utils.isEmpty(producerGroup)) {
            producerGroup = "DEFAULT";
        }

        if (Utils.isEmpty(consumerGroup)) {
            consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }
    }
}
