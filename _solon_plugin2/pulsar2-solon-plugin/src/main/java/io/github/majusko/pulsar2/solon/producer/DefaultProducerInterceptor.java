package io.github.majusko.pulsar2.solon.producer;

import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProducerInterceptor implements ProducerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProducerInterceptor.class);

    @Override
    public void close() {
        logger.debug("DefaultProducerInterceptor closed");
    }

    @Override
    public boolean eligible(Message message) {
        return true;
    }

    @Override
    public Message beforeSend(Producer producer, Message message) {
        logger.debug("[Pulsar producer log:BeforeSend] ProducerName:[{}], Topic:[{}]",
            producer.getProducerName(),
            producer.getTopic());
        return message;
    }

    @Override
    public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId, Throwable exception) {
        if (exception != null) {
            logger.error("[Pulsar producer log:OnSendAcknowledgement] Producer:[{}], Topic:[{}], Payload:[{}], msgID:[{}], exception:[{}]",
                producer.getProducerName(), producer.getTopic(), message.getValue().toString(), msgId.toString(), exception);
            return;
        }
        logger.debug("[Pulsar producer log:OnSendAcknowledgement] Producer:[{}], Topic:[{}] msgID:[{}]",
            producer.getProducerName(), producer.getTopic(), msgId.toString());
    }

    @Override
    public void onPartitionsChange(String topicName, int partitions) {
        logger.debug("[Pulsar producer log:OnPartitionsChange] Topic:[{}], Partitions:[{}]", topicName, partitions);
    }
}
