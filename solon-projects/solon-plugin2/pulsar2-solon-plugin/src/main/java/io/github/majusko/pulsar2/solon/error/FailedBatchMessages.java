package io.github.majusko.pulsar2.solon.error;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Messages;

public class FailedBatchMessages extends FailedMessage{
    private final Messages<?> batchMessages;

    public FailedBatchMessages(Exception exception, Consumer<?> consumer, Messages<?> batchMessages) {
        super(exception, consumer, null);
        this.batchMessages = batchMessages;
    }


    public Messages<?> getBatchMessages() {
        return batchMessages;
    }
}
