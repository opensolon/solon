package io.github.majusko.pulsar2.solon.consumer;

import java.util.Set;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConsumerInterceptor<T> implements ConsumerInterceptor<T> {

    private final static Logger logger = LoggerFactory.getLogger(DefaultConsumerInterceptor.class);

    @Override
    public void close() {
        logger.debug("DefaultConsumerInterceptor closed");
    }

    @Override
    public Message<T> beforeConsume(Consumer<T> consumer, Message<T> message) {
        logger.debug("[Pulsar consumer log:BeforeConsume] ProducerName[{}], ConsumerName:[{}], Topic:[{}], msgID:[{}]," +
                        " MessageKey:[{}], PublishTime:[{}], RedeliveryCount:[{}], GetReplicatedFrom:[{}]",
                message.getProducerName(), consumer.getConsumerName(), message.getTopicName(), message.getMessageId(),
                message.getKey(), message.getPublishTime(), message.getRedeliveryCount(), message.getReplicatedFrom());

        return message;
    }

    @Override
    public void onAcknowledge(Consumer<T> consumer, MessageId messageId, Throwable exception) {
        if (exception != null) {
            logger.debug("[Pulsar consumer log:OnAcknowledge] ConsumerName:[{}], msgID:[{}], exception:[{}]", consumer.getConsumerName(), messageId, exception);
            return;
        }
        logger.debug("[Pulsar consumer log:OnAcknowledge] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName(), messageId);
    }

    @Override
    public void onAcknowledgeCumulative(Consumer<T> consumer, MessageId messageId, Throwable exception) {
        if (exception != null) {
            logger.debug("[Pulsar consumer log:OnAcknowledgeCumulative] ConsumerName:[{}], msgID:[{}], exception:[{}]", consumer.getConsumerName(), messageId, exception);
            return;
        }
        logger.debug("[Pulsar consumer log:OnAcknowledgeCumulative] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName(), messageId);
    }

    @Override
    public void onNegativeAcksSend(Consumer<T> consumer, Set<MessageId> messageIds) {
        logger.debug("[Pulsar consumer log:OnNegativeAcksSend] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName(), messageIds);
    }

    @Override
    public void onAckTimeoutSend(Consumer<T> consumer, Set<MessageId> messageIds) {
        logger.debug("[Pulsar consumer log:OnAckTimeoutSend] ConsumerName:[{}], msgID:[{}]", consumer.getConsumerName(), messageIds);
    }

    @Override
    public void onPartitionsChange(String topicName, int partitions) {
        logger.debug("[Pulsar consumer log:OnPartitionsChange] Topic:[{}], Partitions:[{}]", topicName, partitions);
    }
}
