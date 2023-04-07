package io.github.majusko.pulsar2.solon.producer;

import java.util.HashMap;
import java.util.Map;

import io.github.majusko.pulsar2.solon.annotation.PulsarProducer;
import io.github.majusko.pulsar2.solon.constant.Serialization;

@PulsarProducer
public class ProducerFactory implements PulsarProducerFactory {
    private final Map<String, ProducerMaker> topics = new HashMap<>();

    public ProducerFactory addProducer(String topic) {
        return addProducer(topic, byte[].class, Serialization.BYTE);
    }

    public ProducerFactory addProducer(String topic, Class<?> clazz) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(Serialization.JSON));
        return this;
    }

    public ProducerFactory addProducer(String topic, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(serialization));
        return this;
    }

    public ProducerFactory addProducer(String topic, String namespace, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(serialization).setNamespace(namespace));
        return this;
    }

    public ProducerFactory addProducer(String topic, String namespace, Class<?> clazz) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(Serialization.JSON).setNamespace(namespace));
        return this;
    }

    public ProducerFactory addProducer(ProducerMaker maker) {
        topics.put(maker.getTopic(), maker);
        return this;
    }

    @Override
    public Map<String, ProducerMaker> getTopics() {
        return topics;
    }
}
