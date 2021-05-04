package org.noear.solon.cloud.extend.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.kafka.KafkaProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.ext.WarnThrowable;

import java.io.EOFException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 1.3
 */
public class CloudEventServiceKafkaImp implements CloudEventService {
    private static CloudEventServiceKafkaImp instance;

    public static synchronized CloudEventServiceKafkaImp getInstance() {
        if (instance == null) {
            instance = new CloudEventServiceKafkaImp();
        }

        return instance;
    }


    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    private long timeout;
    private String server;
    String eventChannelName;

    public CloudEventServiceKafkaImp() {
        this.timeout = KafkaProps.instance.getEventPublishTimeout();
        this.server = KafkaProps.instance.getEventServer();
        this.eventChannelName = KafkaProps.instance.getEventChannel();
    }

    private synchronized void initProducer() {
        if (producer != null) {
            return;
        }

        Properties props = new Properties();

        props.put("bootstrap.servers", server);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        producer = new KafkaProducer<String, String>(props);
    }

    private synchronized void initConsumer() {
        if (consumer != null) {
            return;
        }

        Properties props = new Properties();

        props.put("bootstrap.servers", server);
        props.put("group.id", Solon.cfg().appGroup() + "::" + Solon.cfg().appName());
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.records", 100);
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        consumer = new KafkaConsumer<String, String>(props);
    }


    @Override
    public boolean publish(Event event) throws CloudEventException {
        initProducer();

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        Future<RecordMetadata> future = producer.send(new ProducerRecord<String, String>(event.topic(), event.key(), event.content()));
        if (timeout > 0 && event.qos() > 0) {
            try {
                future.get(timeout, TimeUnit.MICROSECONDS);
            } catch (Exception e) {
                throw new CloudEventException(e);
            }
        }

        return true;
    }

    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    public void subscribe() {
        try {
            initConsumer();

            //订阅
            if (observerMap.size() > 0) {
                consumer.subscribe(observerMap.keySet());
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
                            .channel(eventChannelName);

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
        CloudEventObserverEntity entity = null;

        entity = observerMap.get(event.topic());
        if (entity != null) {
            isOk = entity.handler(event);
        } else {
            //只需要记录一下
            EventBus.push(new WarnThrowable(event, "There is no observer for this event topic[" + event.topic() + "]"));
        }

        return isOk;
    }
}
