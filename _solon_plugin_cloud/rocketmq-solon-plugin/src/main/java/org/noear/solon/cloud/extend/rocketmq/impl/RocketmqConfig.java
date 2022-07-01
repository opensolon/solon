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
    /**
     * 生产组
     */
    private String producerGroup;

    /**
     * 消费组
     */
    private String consumerGroup;

    private String server;

    private String namespace;

    private long timeout;

    public RocketmqConfig(CloudProps cloudProps) {
        server = cloudProps.getEventServer();
        timeout = cloudProps.getEventPublishTimeout();
        namespace = Solon.cfg().appNamespace();

        producerGroup = cloudProps.getProp(RocketmqProps.PROP_EVENT_producerGroup);
        consumerGroup = cloudProps.getProp(RocketmqProps.PROP_EVENT_consumerGroup);


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
    public String getNamespace() {
        return namespace;
    }

    /**
     * 消费组
     */
    public String getConsumerGroup() {
        return consumerGroup;
    }

    /**
     * 产品组
     */
    public String getProducerGroup() {
        return producerGroup;
    }


    public String getServer() {
        return server;
    }

    public long getTimeout() {
        return timeout;
    }
}
