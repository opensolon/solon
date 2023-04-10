package io.github.majusko.pulsar2.solon.producer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.noear.solon.annotation.Component;

//@Component
public class PulsarTemplate<T> {


    public MessageId send(String topic, T msg) throws PulsarClientException {
        //noinspection unchecked
        return getProducer(topic).send(msg);
    }

    public CompletableFuture<MessageId> sendAsync(String topic, T message) {
        return getProducer(topic).sendAsync(message);
    }

    public TypedMessageBuilder<T> createMessage(String topic, T message) {
        return getProducer(topic).newMessage().value(message);
    }
    
    private Producer getProducer(String topic) {
        return IProducerConst.producers.get(topic);
    }


}
