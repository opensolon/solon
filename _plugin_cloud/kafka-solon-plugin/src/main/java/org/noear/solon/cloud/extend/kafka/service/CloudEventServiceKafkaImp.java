package org.noear.solon.cloud.extend.kafka.service;

import io.netty.handler.codec.string.StringEncoder;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.kafka.KafkaProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventService;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author noear 2021/5/4 created
 */
public class CloudEventServiceKafkaImp implements CloudEventService {
    private final Producer<String, String> producer;
    private long timeout;

    public CloudEventServiceKafkaImp(String server){
        this.timeout = KafkaProps.instance.getEventPublishTimeout();

        Properties props = new Properties();

        props.put("bootstrap.servers", server);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        producer = new KafkaProducer<String, String>(props);
    }


    @Override
    public boolean publish(Event event) throws CloudEventException {
        Future<RecordMetadata> future = producer.send(new ProducerRecord<String, String>(event.topic(), event.key(), event.content()));
        if (event.qos() > 0) {
            try {
                future.get(timeout, TimeUnit.MICROSECONDS);
            } catch (Exception e) {
                throw new CloudEventException(e);
            }
        }

        return true;
    }

    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {

    }
}
