package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.aliyun.ons.OnsProps;

import java.util.Properties;

/**
 * @author cgy
 * @since 1.11
 */
public class OnsConfig {
    private String producerGroup;
    private String consumerGroup;

    private String server;

    private long timeout;

    private String accessKey;
    private String secretKey;


    //默认20 实例的消费线程数
    private Integer consumeThreadNums;

    //默认16 设置消息消费失败的最大重试次数
    private Integer maxReconsumeTimes;

    public OnsConfig(CloudProps cloudProps) {
        server = cloudProps.getEventServer();
        accessKey = cloudProps.getEventAccessKey();
        secretKey = cloudProps.getEventSecretKey();

        timeout = cloudProps.getEventPublishTimeout();

        consumeThreadNums = Integer.valueOf(cloudProps.getValue(OnsProps.PROP_EVENT_consumeThreadNums, "20"));
        maxReconsumeTimes = Integer.valueOf(cloudProps.getValue(OnsProps.PROP_EVENT_maxReconsumeTimes, "16"));

        producerGroup = cloudProps.getValue(OnsProps.PROP_EVENT_producerGroup);
        consumerGroup = cloudProps.getValue(OnsProps.PROP_EVENT_consumerGroup);

        if (Utils.isEmpty(producerGroup)) {
            producerGroup = "DEFAULT";
        }

        if (Utils.isEmpty(consumerGroup)) {
            consumerGroup = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }
    }

    public Properties getProducerProperties() {
        Properties producer = getProperties();
        producer.put(PropertyKeyConst.GROUP_ID, producerGroup);
        producer.put(PropertyKeyConst.SendMsgTimeoutMillis, timeout);
        return producer;
    }

    public Properties getConsumerProperties() {
        Properties consumer = getProperties();
        consumer.put(PropertyKeyConst.GROUP_ID, consumerGroup);
        consumer.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        consumer.put(PropertyKeyConst.ConsumeThreadNums, consumeThreadNums);
        consumer.put(PropertyKeyConst.MaxReconsumeTimes, maxReconsumeTimes);
        return consumer;
    }

    public Properties getProperties() {
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
