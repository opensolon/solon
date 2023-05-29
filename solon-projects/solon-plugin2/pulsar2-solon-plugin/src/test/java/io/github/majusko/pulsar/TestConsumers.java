package io.github.majusko.pulsar;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.SubscriptionType;
import org.junit.jupiter.api.Assertions;
import org.noear.solon.annotation.Component;

import io.github.majusko.pulsar.msg.AvroMsg;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar.msg.ProtoMsg;
import io.github.majusko.pulsar2.solon.PulsarMessage;
import io.github.majusko.pulsar2.solon.annotation.PulsarConsumer;
import io.github.majusko.pulsar2.solon.annotation.SubscriptionProp;
import io.github.majusko.pulsar2.solon.constant.BatchAckMode;
import io.github.majusko.pulsar2.solon.constant.Serialization;

@Component
public class TestConsumers {

    public AtomicBoolean mockTopicListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean mockTopicAsyncListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean mockTopicMessageListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean avroTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean protoTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean byteTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean stringTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean mockRetryCountListenerReceived = new AtomicBoolean(false);
    public AtomicBoolean subscribeToDeadLetterTopicReceived = new AtomicBoolean(false);
    public AtomicBoolean subscribeToCustomSpElTopicConfig = new AtomicBoolean(false);
    public AtomicBoolean subscribeToCustomSpElConsumerAndSubConfig = new AtomicBoolean(false);
    public AtomicBoolean subscribeToSharedTopicSubscription = new AtomicBoolean(false);
    public AtomicBoolean customConsumerTestReceived = new AtomicBoolean(false);
    public AtomicInteger failTwiceRetryCount = new AtomicInteger(0);
    public AtomicInteger topicOverflowDueToExceptionRetryCount = new AtomicInteger(0);
    public AtomicBoolean customConsumerNamespaceReceived = new AtomicBoolean(false);
    public AtomicBoolean batchMessageWithAutoAckReceived = new AtomicBoolean(false);
    public AtomicBoolean batchMessageWithAckListReceived = new AtomicBoolean(false);
    public AtomicBoolean batchMessageWithManualAckReceived = new AtomicBoolean(false);

    public AtomicBoolean subscriptionPropertiesReceived = new AtomicBoolean(false);

    public static final String CUSTOM_CONSUMER_NAME = "custom-consumer-name";
    public static final String CUSTOM_SUBSCRIPTION_NAME= "custom-subscription-name";
    public static final String CUSTOM_CONSUMER_TOPIC = "custom-consumer-topic";
    public static final String CUSTOM_SUB_AND_CONSUMER_TOPIC = "custom-sub-and-consumer";
    public static final String SHARED_SUB_TEST = "shared-sub-consumer";
    public static final String EXCLUSIVE_SUB_TEST = "exclusive-sub-consumer";
    public static final String CUSTOM_NAMESPACE_TOPIC = "custom-namespace-name";
    public static final String CUSTOM_BATCH_CONSUMER_TOPIC_AUTO_ACK = "custom-batch-consumer-auto-ack-topic";
    public static final String CUSTOM_BATCH_CONSUMER_TOPIC_ACK_FROM_LIST = "custom-batch-consumer-auto-ack-list-topic";
    public static final String CUSTOM_BATCH_CONSUMER_TOPIC_MANUAL_ACK = "custom-batch-consumer-manual-ack-topic";

    public static final String SUBSCRIPTION_PROPERTIES = "subscription-properties-topic";

    public static final String TEST_KEY = "test-key";

    public static final String TEST_VALUE = "test-value";

    @PulsarConsumer(topic = "topic-one", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicOneListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        mockTopicListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-for-error", clazz = String.class, serialization = Serialization.JSON)
    public void topicForErrorListener(Integer myMsg) {
    }

    @PulsarConsumer(topic = "topic-for-error-2", clazz = String.class, serialization = Serialization.JSON)
    public void topicForError2Listener(String myMsg) throws Exception {
        throw new MyCustomException("Random exception");
    }

    @PulsarConsumer(topic = "topic-avro", clazz = AvroMsg.class, serialization = Serialization.AVRO)
    public void avroTopic(AvroMsg avroMsg) {
        Assertions.assertNotNull(avroMsg);
        avroTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-proto", clazz = ProtoMsg.class, serialization = Serialization.PROTOBUF)
    public void protoTopic(ProtoMsg protoMsg) {
        Assertions.assertNotNull(protoMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, protoMsg.getData());

        protoTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-byte")
    public void byteTopic(byte[] byteMsg) {
        Assertions.assertNotNull(byteMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, new String(byteMsg, StandardCharsets.UTF_8));

        byteTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-string", clazz = String.class, serialization = Serialization.STRING)
    public void byteTopic(String stringMsg) {
        Assertions.assertNotNull(stringMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, stringMsg);

        stringTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-async", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicAsyncListener(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        mockTopicAsyncListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-message", clazz = MyMsg.class, serialization = Serialization.JSON)
    public void topicMessageListener(PulsarMessage<MyMsg> myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertNotNull(myMsg.getProducerName());
        Assertions.assertNotNull(myMsg.getProperties());
        Assertions.assertNotNull(myMsg.getKey());
        Assertions.assertNotNull(myMsg.getSequenceId());
        Assertions.assertNotNull(myMsg.getPublishTime());
        Assertions.assertNotNull(myMsg.getTopicName());
        Assertions.assertNotNull(myMsg.getMessageId());
        mockTopicMessageListenerReceived.set(true);
    }

    @PulsarConsumer(topic = "topic-retry", clazz = MyMsg.class, maxRedeliverCount = 3, subscriptionType = SubscriptionType.Shared)
    public void failTwice(MyMsg myMsg) throws Exception {
        int retryAttempt = failTwiceRetryCount.getAndIncrement();

        if(retryAttempt < 2) {
            throw new Exception("Expected msg fail.");
        }
        Assertions.assertNotNull(myMsg);
        mockRetryCountListenerReceived.set(true);
    }


    @PulsarConsumer(topic = "topic-deliver-to-dead-letter", clazz = MyMsg.class, subscriptionType = SubscriptionType.Shared, deadLetterTopic = "custom-dead-letter-topic")
    public void topicOverflowDueToException(MyMsg myMsg) throws Exception {
        int retryAttempt = topicOverflowDueToExceptionRetryCount.getAndIncrement();

        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());

        if(retryAttempt < 2) {
            throw new Exception("Expected msg fail.");
        }
        Assertions.fail();
    }

    @PulsarConsumer(topic = "custom-dead-letter-topic", clazz = MyMsg.class)
    public void subscribeToDeadLetterTopic(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        subscribeToDeadLetterTopicReceived.set(true);
    }

    @PulsarConsumer(topic = "${my.custom.topic.name}", clazz = MyMsg.class)
    public void subscribeToCustomTopicName(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        subscribeToCustomSpElTopicConfig.set(true);
    }

    @PulsarConsumer(
        topic = CUSTOM_CONSUMER_TOPIC,
        clazz = MyMsg.class,
        consumerName = CUSTOM_CONSUMER_NAME,
        subscriptionName = CUSTOM_SUBSCRIPTION_NAME)
    public void customConsumer(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        customConsumerTestReceived.set(true);
    }

    @PulsarConsumer(
        topic = CUSTOM_SUB_AND_CONSUMER_TOPIC,
        clazz = MyMsg.class,
        consumerName = "${my.custom.consumer.name}",
        subscriptionName = "${my.custom.subscription.name}")
    public void subscribeToCustomSubAndConsumer(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        subscribeToCustomSpElConsumerAndSubConfig.set(true);
    }

    @PulsarConsumer(
        topic = SHARED_SUB_TEST,
        clazz = MyMsg.class,
        subscriptionType = SubscriptionType.Shared)
    public void sharedTopicSubscription(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        subscribeToSharedTopicSubscription.set(true);
    }

    @PulsarConsumer(
        topic = EXCLUSIVE_SUB_TEST,
        clazz = MyMsg.class,
        subscriptionType = SubscriptionType.Exclusive)
    public void exclusiveTopicSubscription(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        subscribeToSharedTopicSubscription.set(true);
    }

    @PulsarConsumer(
        topic = CUSTOM_NAMESPACE_TOPIC,
        clazz = MyMsg.class,
        subscriptionType = SubscriptionType.Exclusive,
        namespace = "default")
    public void customConsumerNamespace(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        customConsumerNamespaceReceived.set(true);
    }
    
    @PulsarConsumer(
            topic = CUSTOM_BATCH_CONSUMER_TOPIC_AUTO_ACK,
            clazz = MyMsg.class,
            subscriptionType = SubscriptionType.Shared,
            batch = true,
            maxNumMessage = 10,
            timeoutMillis = 10000
            )
        public void batchConsumerAutoAck(Messages<MyMsg> msgs) {
            Assertions.assertNotNull(msgs);
            Assertions.assertEquals(msgs.size(),10);
            msgs.forEach((msg) -> {
                System.out.println(msg.getValue());
                Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, msg.getValue().getData());
            });
            
            batchMessageWithAutoAckReceived.set(true);
    }
    
    @PulsarConsumer(
            topic =CUSTOM_BATCH_CONSUMER_TOPIC_ACK_FROM_LIST,
            clazz = MyMsg.class,
            subscriptionType = SubscriptionType.Shared,
            batch = true,
            maxNumMessage = 10,
            timeoutMillis = 10000
            )
        public List<MessageId> batchConsumerAutoAckFromList(Messages<MyMsg> msgs) {
            Assertions.assertNotNull(msgs);
            Assertions.assertEquals(msgs.size(),10);
            List<MessageId> ackList = new ArrayList<>();
            int i = 0;
            msgs.forEach((msg) -> {
                Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, msg.getValue().getData());
                ackList.add(msg.getMessageId());
            });
            batchMessageWithAckListReceived.set(true);
            return ackList;
    }    
    
    @PulsarConsumer(
            topic =CUSTOM_BATCH_CONSUMER_TOPIC_MANUAL_ACK,
            clazz = MyMsg.class,
            subscriptionType = SubscriptionType.Shared,
            batch = true,
            batchAckMode = BatchAckMode.MANUAL,
            maxNumMessage = 10,
            timeoutMillis = 10000
            )
        public void batchConsumerManualAck(Messages<MyMsg> msgs,Consumer<MyMsg> consumer) {
            Assertions.assertNotNull(msgs);
            Assertions.assertEquals(msgs.size(),10);
            int i = 0;
            msgs.forEach((msg) -> {
                Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, msg.getValue().getData());
                try {
                    consumer.acknowledge(msg.getMessageId());
                } catch (Exception e) {
                   e.printStackTrace();
                }
            });
            batchMessageWithManualAckReceived.set(true);
    }

    @PulsarConsumer(topic = SUBSCRIPTION_PROPERTIES, clazz = MyMsg.class, subscriptionProperties = {@SubscriptionProp(key=TEST_KEY, value=TEST_VALUE)})
    public void subscriptionPropertiesConsumer(MyMsg myMsg) {
        Assertions.assertNotNull(myMsg);
        Assertions.assertEquals(PulsarJavaSpringBootStarterApplicationTests.VALIDATION_STRING, myMsg.getData());
        subscriptionPropertiesReceived.set(true);
    }
}
