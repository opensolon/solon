package io.github.majusko.pulsar2.solon.producer;

import java.util.Map;

public interface PulsarProducerFactory {
    Map<String, ProducerMaker> getTopics();
}
