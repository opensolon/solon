package io.github.majusko.pulsar2.solon.reactor;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;

public class FluxConsumerHolder {

    private final Consumer<?> consumer;
    private final Message<?> message;

    public FluxConsumerHolder(Consumer<?> consumer, Message<?> message) {
        this.consumer = consumer;
        this.message = message;
    }

    public Consumer<?> getConsumer() {
        return consumer;
    }

    public Message<?> getMessage() {
        return message;
    }
}
