package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;

import java.util.Properties;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsConfig {
    private static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    private static final String PROP_EVENT_producerGroup = "event.producerGroup";

    private static final String PROP_EVENT_consumeThreadNums = "event.consumeThreadNums";
    private static final String PROP_EVENT_maxReconsumeTimes = "event.maxReconsumeTimes";

    private final String channelName;
    private final String server;

    private final long timeout;

    private String producerGroup;
    private String consumerGroup;

    private final String accessKey;
    private final String secretKey;


    //实例的消费线程数，0表示默认
    private final int consumeThreadNums;

    //设置消息消费失败的最大重试次数，0表示默认
    private final int maxReconsumeTimes;

    public OnsConfig(CloudProps cloudProps) {
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
    }

    public String getChannelName() {
        return channelName;
    }

    public Properties getProducerProperties() {
        Properties producer = getBaseProperties();
        producer.put(PropertyKeyConst.GROUP_ID, producerGroup);
        producer.put(PropertyKeyConst.SendMsgTimeoutMillis, timeout);
        return producer;
    }

    public Properties getConsumerProperties() {
        Properties consumer = getBaseProperties();
        consumer.put(PropertyKeyConst.GROUP_ID, consumerGroup);
        //只能是集群模式
        consumer.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        //实例的消费线程数
        if (consumeThreadNums > 0) {
            consumer.put(PropertyKeyConst.ConsumeThreadNums, consumeThreadNums);
        }
        //设置消息消费失败的最大重试次数
        if (maxReconsumeTimes > 0) {
            consumer.put(PropertyKeyConst.MaxReconsumeTimes, maxReconsumeTimes);
        }
        return consumer;
    }

    public Properties getBaseProperties() {
        Properties properties = new Properties();

        if (Utils.isNotEmpty(accessKey)) {
            properties.put(PropertyKeyConst.AccessKey, accessKey);
        }
        if (Utils.isNotEmpty(secretKey)) {
            properties.put(PropertyKeyConst.SecretKey, secretKey);
        }

        properties.put(PropertyKeyConst.NAMESRV_ADDR, server);
        return properties;
    }
}
