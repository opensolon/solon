package io.github.majusko.pulsar;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.noear.solon.annotation.Component;

import io.github.majusko.pulsar2.solon.consumer.DefaultConsumerInterceptor;

@Component
public class TestConsumerInterceptor extends DefaultConsumerInterceptor<Object> {

    public AtomicBoolean beforeConsumeReceived = new AtomicBoolean(false);
    public AtomicBoolean onAcknowledgeReceived = new AtomicBoolean(false);
    public AtomicBoolean onAckTimeoutSendReceived = new AtomicBoolean(false);

    @Override
    public Message<Object> beforeConsume(Consumer<Object> consumer, Message<Object> message) {
        beforeConsumeReceived.set(true);
        return super.beforeConsume(consumer, message);
    }

    @Override
    public void onAcknowledge(Consumer<Object> consumer, MessageId messageId, Throwable exception) {
        onAcknowledgeReceived.set(true);
        super.onAcknowledge(consumer, messageId, exception);
    }

    @Override
    public void onAckTimeoutSend(Consumer<Object> consumer, Set<MessageId> messageIds) {
        onAckTimeoutSendReceived.set(true);
        super.onAckTimeoutSend(consumer, messageIds);
    }
}