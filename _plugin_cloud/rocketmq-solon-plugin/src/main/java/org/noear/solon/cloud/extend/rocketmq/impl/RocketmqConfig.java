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
    public String producerGroup;

    /**
     * 消费组
     */
    public String consumerGroup;

    public String server;

    public RocketmqConfig() {
        producerGroup = RocketmqProps.instance.getEventExchange();

        if (Utils.isEmpty(producerGroup)) {
            producerGroup = "DEFAULT";
        }

        consumerGroup = RocketmqProps.instance.getEventQueue();

        if (Utils.isEmpty(consumerGroup)) {
            consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }
    }
}
