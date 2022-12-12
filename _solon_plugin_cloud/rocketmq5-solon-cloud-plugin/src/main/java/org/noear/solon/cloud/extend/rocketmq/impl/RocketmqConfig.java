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
    private static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    private static final String PROP_EVENT_producerGroup = "event.producerGroup";

    private static final String PROP_EVENT_consumeThreadNums = "event.consumeThreadNums";
    private static final String PROP_EVENT_maxReconsumeTimes = "event.maxReconsumeTimes";

    private String producerGroup;
    private String consumerGroup;

    private final String channelName;
    private final String server;

    private final long timeout;

    //默认20 实例的消费线程数
    private final int consumeThreadNums;

    //默认16 设置消息消费失败的最大重试次数
    private final int maxReconsumeTimes;

    public RocketmqConfig(CloudProps cloudProps) {
        server = cloudProps.getEventServer();
        channelName = cloudProps.getEventChannel();
        timeout = cloudProps.getEventPublishTimeout();

        consumeThreadNums = Integer.valueOf(cloudProps.getValue(PROP_EVENT_consumeThreadNums, "20"));
        maxReconsumeTimes = Integer.valueOf(cloudProps.getValue(PROP_EVENT_maxReconsumeTimes, "16"));


        producerGroup = cloudProps.getValue(PROP_EVENT_producerGroup);
        consumerGroup = cloudProps.getValue(PROP_EVENT_consumerGroup);


        if (Utils.isEmpty(producerGroup)) {
            producerGroup = "DEFAULT";
        }

        if (Utils.isEmpty(consumerGroup)) {
            consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }
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

    public int getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }

    public int getConsumeThreadNums() {
        return consumeThreadNums;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getServer() {
        return server;
    }

    public long getTimeout() {
        return timeout;
    }
}
