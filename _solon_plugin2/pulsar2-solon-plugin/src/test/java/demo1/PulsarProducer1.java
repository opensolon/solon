package demo1;

import java.util.HashMap;
import java.util.Map;

import io.github.majusko.pulsar2.solon.annotation.PulsarProducer;
import io.github.majusko.pulsar2.solon.constant.Serialization;
import io.github.majusko.pulsar2.solon.producer.ProducerMaker;
import io.github.majusko.pulsar2.solon.producer.PulsarProducerFactory;

//@PulsarProducer
public class PulsarProducer1 implements PulsarProducerFactory{
	private final Map<String, ProducerMaker> topics = new HashMap<>();

    public PulsarProducer1 addProducer(String topic) {
        return addProducer(topic, byte[].class, Serialization.BYTE);
    }

    public PulsarProducer1 addProducer(String topic, Class<?> clazz) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(Serialization.JSON));
        return this;
    }

    public PulsarProducer1 addProducer(String topic, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(serialization));
        return this;
    }

    public PulsarProducer1 addProducer(String topic, String namespace, Class<?> clazz, Serialization serialization) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(serialization).setNamespace(namespace));
        return this;
    }

    public PulsarProducer1 addProducer(String topic, String namespace, Class<?> clazz) {
        topics.put(topic, new ProducerMaker(topic, clazz).setSerialization(Serialization.JSON).setNamespace(namespace));
        return this;
    }

    public PulsarProducer1 addProducer(ProducerMaker maker) {
        topics.put(maker.getTopic(), maker);
        return this;
    }

    @Override
    public Map<String, ProducerMaker> getTopics() {
        return topics;
    }
}
