package org.noear.solon.cloud.extend.rocketmq.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConfig {
    private CloudProps cloudProps;
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

    public RocketmqConfig(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        server = cloudProps.getEventServer();

        timeout = cloudProps.getEventPublishTimeout();

        namespace = getEventNamespace();
        producerGroup = getEventProducerGroup();
        consumerGroup = getEventConsumerGroup();

        if (Utils.isEmpty(producerGroup)) {
            producerGroup = "DEFAULT";
        }

        if (Utils.isEmpty(consumerGroup)) {
            consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }
    }

    /**
     * 命名空间
     */
    public String getEventNamespace() {
        return cloudProps.getProp("event.namespace");
    }

    /**
     * 消费组
     */
    public String getEventConsumerGroup() {
        return cloudProps.getProp("event.consumerGroup");
    }

    /**
     * 产品组
     */
    public String getEventProducerGroup() {
        return cloudProps.getProp("event.producerGroup");
    }
}
