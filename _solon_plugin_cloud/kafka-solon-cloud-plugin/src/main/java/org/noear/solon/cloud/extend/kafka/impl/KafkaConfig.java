package org.noear.solon.cloud.extend.kafka.impl;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

import java.util.Properties;

/**
 * @author noear 2022/12/15 created
 */
public class KafkaConfig {
    private final CloudProps cloudProps;
    private final String server;
    private final long timeout;

    private final String eventChannel;
    private final String eventGroup;


    public long getTimeout() {
        return timeout;
    }

    public String getEventChannel() {
        return eventChannel;
    }

    public String getEventGroup() {
        return eventGroup;
    }

    public KafkaConfig(CloudProps cloudProps){
        this.cloudProps = cloudProps;

        server = cloudProps.getEventServer();
        timeout = cloudProps.getEventPublishTimeout();

        eventChannel = cloudProps.getEventChannel();
        eventGroup = cloudProps.getEventGroup();
    }

    public Properties getProducerProperties() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", server);
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384); //默认是16384Bytes，即16kB

        //绑定定制属性
        Properties props = cloudProps.getEventProducerProps();
        if (props.size() > 0) {
            props.forEach((k, v) -> {
                properties.put(k, v);
            });
        }

        return properties;
    }

    public Properties getConsumerProperties() {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", server);
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        properties.put("group.id", Solon.cfg().appGroup() + "_" + Solon.cfg().appName());
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("max.poll.records", 100);
        properties.put("auto.offset.reset", "earliest");

        //绑定定制属性
        Properties props = cloudProps.getEventConsumerProps();
        if (props.size() > 0) {
            props.forEach((k, v) -> {
                properties.put(k, v);
            });
        }

        return properties;
    }
}
