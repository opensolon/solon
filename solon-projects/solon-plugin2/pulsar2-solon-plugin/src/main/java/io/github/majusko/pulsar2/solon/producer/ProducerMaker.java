package io.github.majusko.pulsar2.solon.producer;

import org.apache.pulsar.client.api.CompressionType;

import io.github.majusko.pulsar2.solon.constant.Serialization;

import java.util.Optional;

public class ProducerMaker {

    private final String topic;
    private final Class<?> clazz;

    private String namespace;

    private Serialization serialization = Serialization.JSON;

    private CompressionType compressionType = CompressionType.NONE;

    public ProducerMaker(String topic, Class<?> clazz) {
        this.topic = topic;
        this.clazz = clazz;
    }

    public ProducerMaker setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public ProducerMaker setSerialization(Serialization serialization) {
        this.serialization = serialization;
        return this;
    }

    public ProducerMaker setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    public Serialization getSerialization() {
        return serialization;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }
}
