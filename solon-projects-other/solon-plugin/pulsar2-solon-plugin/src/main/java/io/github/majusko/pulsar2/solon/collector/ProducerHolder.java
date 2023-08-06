package io.github.majusko.pulsar2.solon.collector;

import org.apache.pulsar.client.api.CompressionType;

import io.github.majusko.pulsar2.solon.constant.Serialization;

import java.util.Optional;

public class ProducerHolder {

    private final String topic;
    private final Class<?> clazz;
    private final Serialization serialization;
    private String namespace;
    private final CompressionType compressionType;

    public ProducerHolder(String topic, Class<?> clazz, Serialization serialization, CompressionType compressionType) {
        this.topic = topic;
        this.clazz = clazz;
        this.serialization = serialization;
        this.compressionType = compressionType;
    }

    public ProducerHolder(String topic, Class<?> clazz, Serialization serialization, String namespace, CompressionType compressionType) {
        this(topic, clazz, serialization, compressionType);
        this.namespace =  namespace;
    }

    public String getTopic() {
        return topic;
    }

    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Serialization getSerialization() {
        return serialization;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }
}
