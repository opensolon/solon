package org.noear.solon.cloud.extend.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.kafka.impl.KafkaConfig;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventServiceKafkaImp implements CloudEventServicePlus {
    static Logger log = LoggerFactory.getLogger(CloudEventServiceKafkaImp.class);

    private final KafkaConfig config;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    public CloudEventServiceKafkaImp(CloudProps cloudProps) {
        this.config = new KafkaConfig(cloudProps);
    }

    private synchronized void initProducer() {
        if (producer != null) {
            return;
        }

        Properties properties = config.getProducerProperties();
        producer = new KafkaProducer<>(properties);
    }

    private synchronized void initConsumer() {
        if (consumer != null) {
            return;
        }

        Properties properties = config.getConsumerProperties();
        consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public boolean publish(Event event) throws CloudEventException {
        initProducer();

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        Future<RecordMetadata> future = producer.send(new ProducerRecord<>(event.topic(), event.key(), event.content()));
        if (config.getTimeout() > 0 && event.qos() > 0) {
            try {
                future.get(config.getTimeout(), TimeUnit.MICROSECONDS);
            } catch (Exception e) {
                throw new CloudEventException(e);
            }
        }

        return true;
    }

    CloudEventObserverManger observerManger = new CloudEventObserverManger();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, String tag, CloudEventHandler observer) {
        observerManger.add(topic, level, group, topic, tag, observer);
    }

    public void subscribe() {
        try {
            initConsumer();

            //订阅
            if (observerManger.topicSize() > 0) {
                consumer.subscribe(observerManger.topicAll());
            }

            //开始拉取
            new Thread(this::subscribePull).start();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private void subscribePull() {
        while (true) {
            boolean isOk = true;

            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    Thread.sleep(100);
                    continue;
                }

                for (ConsumerRecord<String, String> record : records) {
                    Event event = new Event(record.topic(), record.value())
                            .key(record.key())
                            .channel(config.getEventChannel());

                    isOk = isOk && onReceive(event);
                }

                if (isOk) {
                    consumer.commitAsync();
                }
            } catch (EOFException e) {
                break;
            } catch (Throwable e) {
                EventBus.push(e);
            }
        }
    }

    /**
     * 处理接收事件
     */
    public boolean onReceive(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        handler = observerManger.getByTopic(event.topic());
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", event.topic());
        }

        return isOk;
    }

    @Override
    public String getChannel() {
        return config.getEventChannel();
    }

    @Override
    public String getGroup() {
        return config.getEventGroup();
    }
}
