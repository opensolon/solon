package org.noear.solon.cloud.extend.rocketmq.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.3
 * @since 1.11
 */
public class RocketmqConfig {
    static final Logger log = LoggerFactory.getLogger(RocketmqConfig.class);

    private static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    private static final String PROP_EVENT_producerGroup = "event.producerGroup";

    private static final String PROP_EVENT_consumeThreadNums = "event.consumeThreadNums";
    private static final String PROP_EVENT_maxReconsumeTimes = "event.maxReconsumeTimes";

    private String producerGroup;
    private String consumerGroup;

    private final String channelName;
    private final String server;

    private final String accessKey;
    private final String secretKey;

    private final long timeout;

    //实例的消费线程数，0表示默认
    private final int consumeThreadNums;

    //设置消息消费失败的最大重试次数，0表示默认
    private final int maxReconsumeTimes;

    public RocketmqConfig(CloudProps cloudProps) {
        server = cloudProps.getEventServer();
        channelName = cloudProps.getEventChannel();

        accessKey = cloudProps.getEventAccessKey();
        secretKey = cloudProps.getEventSecretKey();

        timeout = cloudProps.getEventPublishTimeout();

        consumeThreadNums = Integer.valueOf(cloudProps.getValue(PROP_EVENT_consumeThreadNums, "0"));
        maxReconsumeTimes = Integer.valueOf(cloudProps.getValue(PROP_EVENT_maxReconsumeTimes, "0"));


        producerGroup = cloudProps.getValue(PROP_EVENT_producerGroup);
        consumerGroup = cloudProps.getValue(PROP_EVENT_consumerGroup);


        if (Utils.isEmpty(producerGroup)) {
            producerGroup = "DEFAULT";
        }

        if (Utils.isEmpty(consumerGroup)) {
            consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }


        log.trace("producerGroup=" + producerGroup);
        log.trace("consumerGroup=" + consumerGroup);
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

    /**
     * 实例的消费线程数，0表示默认
     * */
    public int getConsumeThreadNums() {
        return consumeThreadNums;
    }

    /**
     * 设置消息消费失败的最大重试次数，0表示默认
     * */
    public int getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }



    public String getChannelName() {
        return channelName;
    }

    public String getServer() {
        return server;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public long getTimeout() {
        return timeout;
    }
}
