package io.github.majusko.pulsar2.solon.error;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;

public class FailedMessage {
    private final Throwable exception;
    private final Consumer<?> consumer;
    private final Message<?> message;
    private final Throwable consumerException;

    public FailedMessage(Throwable exception, Consumer<?> consumer, Message<?> message) {
        this.exception = exception;
        this.consumer = consumer;
        this.message = message;
        this.consumerException = exception.getCause();
    }

    public Throwable getException() {
        return exception;
    }

    public Consumer<?> getConsumer() {
        return consumer;
    }

    public Message<?> getMessage() {
        return message;
    }

    public Throwable getConsumerException() {
        return consumerException;
    }
}
