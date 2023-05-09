package io.github.majusko.pulsar;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.noear.solon.annotation.Component;

import io.github.majusko.pulsar2.solon.producer.DefaultProducerInterceptor;

@Component
public class TestProducerInterceptor extends DefaultProducerInterceptor {

    public AtomicBoolean eligibleReceived = new AtomicBoolean(false);
    public AtomicBoolean beforeSendReceived = new AtomicBoolean(false);
    public AtomicBoolean onSendAcknowledgementReceived = new AtomicBoolean(false);

    @Override
    public boolean eligible(Message message) {
        eligibleReceived.set(true);
        return super.eligible(message);
    }

    @Override
    public Message beforeSend(Producer producer, Message message) {
        beforeSendReceived.set(true);
        return super.beforeSend(producer, message);
    }

    @Override
    public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId, Throwable exception) {
        onSendAcknowledgementReceived.set(true);
        super.onSendAcknowledgement(producer, message, msgId, exception);
    }
}