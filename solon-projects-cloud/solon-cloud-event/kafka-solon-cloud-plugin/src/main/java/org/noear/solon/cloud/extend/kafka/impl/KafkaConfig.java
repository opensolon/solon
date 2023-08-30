package org.noear.solon.cloud.extend.kafka.impl;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

import java.util.Properties;

/**
 * @author noear
 * @since 1.11
 */
public class KafkaConfig {
    private final CloudProps cloudProps;
    private final String server;
    private final long publishTimeout;

    private final String eventChannel;
    private final String eventGroup;


    public long getPublishTimeout() {
        return publishTimeout;
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
        publishTimeout = cloudProps.getEventPublishTimeout();

        eventChannel = cloudProps.getEventChannel();
        eventGroup = cloudProps.getEventGroup();
    }

    public Properties getProducerProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); //默认是16384Bytes，即16kB

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

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, Solon.cfg().appGroup() + "_" + Solon.cfg().appName());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 100);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

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
