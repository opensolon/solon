package io.github.majusko.pulsar2.solon;

import demo.DemoApp;
import io.github.majusko.pulsar.TestConsumerInterceptor;
import io.github.majusko.pulsar.TestConsumers;
import io.github.majusko.pulsar.TestFluxConsumersConfiguration;
import io.github.majusko.pulsar.TestProducerInterceptor;
import io.github.majusko.pulsar.msg.AvroMsg;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar.msg.ProtoMsg;
import io.github.majusko.pulsar2.solon.collector.ConsumerCollector;
import io.github.majusko.pulsar2.solon.collector.ConsumerHolder;
import io.github.majusko.pulsar2.solon.consumer.ConsumerAggregator;
import io.github.majusko.pulsar2.solon.producer.ProducerFactory;
import io.github.majusko.pulsar2.solon.producer.ProducerMaker;
import io.github.majusko.pulsar2.solon.producer.PulsarTemplate;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumer;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumerFactory;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumerHolder;
import io.github.majusko.pulsar2.solon.utils.UrlBuildService;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.ConsumerBase;
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import org.testcontainers.utility.DockerImageName;
import reactor.core.Disposable;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

@Import(scanPackages = {"io.github.majusko.pulsar"})
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(DemoApp.class)
public class DemoSimpleTests {
	@Inject
    private ConsumerAggregator consumerAggregator;

    @Inject
    private ConsumerCollector consumerCollector;

    @Inject
    private ProducerFactory producerFactory;

    @Inject
    private PulsarTemplate<MyMsg> producer;

    @Inject
    private PulsarTemplate<String> producerForError;

    @Inject
    private PulsarTemplate<AvroMsg> producerForAvroTopic;

    @Inject
    private PulsarTemplate<ProtoMsg> producerForProtoTopic;

    @Inject
    private PulsarTemplate<byte[]> producerForByteTopic;

    @Inject
    private PulsarTemplate<String> producerForStringTopic;

    @Inject
    private TestConsumers testConsumers;

    @Inject
    private UrlBuildService urlBuildService;

    @Inject
    private FluxConsumer<MyMsg> myTestFluxConsumer;

    @Inject
    private FluxConsumer<FluxConsumerHolder> robustFluxConsumer;

    @Inject
    private TestConsumerInterceptor testConsumerInterceptor;

    @Inject
    private TestProducerInterceptor testProducerInterceptor;

    @Inject
    private FluxConsumerFactory fluxConsumerFactory;

    @Inject("${my.custom.subscription.name}")
    private String customSubscriptionName;

    @Inject("${my.custom.consumer.name}")
    private String customConsumerName;


    public static final String VALIDATION_STRING = "validation-string";

//    @DynamicPropertySource
//    static void propertySettings(DynamicPropertyRegistry registry) {
//        registry.add("pulsar.serviceUrl", pulsarContainer::getPulsarBrokerUrl);
//    }

    @Test
    public void testProducerSendMethod() throws PulsarClientException {
        producer.send("topic-one", new MyMsg(VALIDATION_STRING));

        await().untilTrue(testConsumers.mockTopicListenerReceived);
    }

    @Test
    void testBasicConsumerInterceptor() throws PulsarClientException {
        producer.send("topic-one", new MyMsg(VALIDATION_STRING));

        await().untilTrue(testProducerInterceptor.beforeSendReceived);
        await().untilTrue(testProducerInterceptor.eligibleReceived);
        await().untilTrue(testProducerInterceptor.onSendAcknowledgementReceived);
        await().untilTrue(testConsumerInterceptor.beforeConsumeReceived);
        await().untilTrue(testConsumerInterceptor.onAcknowledgeReceived);
    }

    @Test
    void testBasicDeadLetterRetryPolicy() throws PulsarClientException {

        producer.send("topic-retry", new MyMsg(VALIDATION_STRING));

        await().untilTrue(testConsumers.mockRetryCountListenerReceived);

        Assertions.assertEquals(3, testConsumers.failTwiceRetryCount.get());

        await().untilTrue(testConsumerInterceptor.onAckTimeoutSendReceived);
    }

    @Test
    void testProducerSendAsyncMethod() throws PulsarClientException {
        producer.sendAsync("topic-async", new MyMsg("async")).thenAccept(messageId -> {
            Assertions.assertNotNull(messageId);
        });

        await().untilTrue(testConsumers.mockTopicAsyncListenerReceived);
    }

    @Test
    void testProducerCreateMessageMethod() throws PulsarClientException {
        producer.createMessage("topic-message", new MyMsg("my-message"))
            .property("my-key", "my-value")
            .property("my-other-key", "my-other-value")
            .sequenceId(123l)
            .key("my-key")
            .send();

        await().untilTrue(testConsumers.mockTopicMessageListenerReceived);
    }

    @Test
    void testConsumerRegistration1() throws Exception {
        final List<Consumer> classicConsumers = consumerAggregator.getConsumers();
        final List<Consumer> fluxConsumers = fluxConsumerFactory.getConsumers();

        Assertions.assertEquals(24, classicConsumers.size() + fluxConsumers.size());

        final Consumer<?> consumer =
            classicConsumers.stream().filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl("topic-one"))).findFirst().orElseThrow(Exception::new);

        Assertions.assertNotNull(consumer);
    }

    @Test
    void testConsumerRegistration2() {
        final Class<TestConsumers> clazz = TestConsumers.class;
        final List<ConsumerHolder> consumerHolders = Arrays.stream(clazz.getMethods())
            .map($ -> consumerCollector.getConsumer(urlBuildService.buildConsumerName(clazz, $)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        Assertions.assertNotNull(consumerHolders);
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getAnnotation().topic().equals("topic-one")));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getAnnotation().topic().equals("topic-for" +
            "-error")));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getBean().getClass().equals(TestConsumers.class)));
        Assertions.assertTrue(consumerHolders.stream().anyMatch($ -> $.getHandler().getName().equals(
            "topicOneListener")));
    }

    @Test
    void testProducerRegistration() {
        final Map<String, ProducerMaker> topics = producerFactory.getTopics();

        Assertions.assertEquals(24, topics.size());

        final Set<String> topicNames = new HashSet<>(topics.keySet());

        Assertions.assertTrue(topicNames.contains("topic-one"));
        Assertions.assertTrue(topicNames.contains("topic-two"));
    }

    @Test
    void testMessageErrorHandling() throws PulsarClientException {
        final AtomicBoolean receivedError = new AtomicBoolean(false);
        final String messageToSend = "This message will never arrive.";
        final Disposable disposable = consumerAggregator.onError(($) -> {
            Assertions.assertEquals($.getConsumer().getTopic(), urlBuildService.buildTopicUrl("topic-for-error"));
            Assertions.assertEquals($.getMessage().getValue(), messageToSend);
            Assertions.assertNotNull($.getException());
            Assertions.assertNull($.getConsumerException());

            receivedError.set(true);
        });

        producerForError.send("topic-for-error", messageToSend);

        await().untilTrue(receivedError);

        disposable.dispose();
    }
    @Test
    void avroSerializationTestOk() throws Exception {
        AvroMsg testAvroMsg = new AvroMsg();
        testAvroMsg.setData("avro-test");
        producerForAvroTopic.send("topic-avro", testAvroMsg);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.avroTopicReceived.get());
    }

    @Test
    void protoSerializationTestOk() throws Exception {
        final ProtoMsg msg = ProtoMsg.newBuilder().setData(VALIDATION_STRING).build();
        producerForProtoTopic.send("topic-proto", msg);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.protoTopicReceived.get());
    }

    @Test
    void byteSerializationTestOk() throws Exception {
        byte[] data = VALIDATION_STRING.getBytes(StandardCharsets.UTF_8);

        producerForByteTopic.send("topic-byte", data);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.byteTopicReceived.get());
    }

    @Test
    void stringSerializationTestOk() throws Exception {
        producerForStringTopic.send("topic-string", VALIDATION_STRING);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.stringTopicReceived.get());
    }

    @Test
    void testSpelSupportConsumerAndProducer() throws Exception {
        producerForStringTopic.send("${my.custom.topic.name}", VALIDATION_STRING);
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToCustomSpElTopicConfig.get());
    }

    @Test
    void dealLetterTopicDelivery() throws Exception {
        producer.send("topic-deliver-to-dead-letter", new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToDeadLetterTopicReceived.get());
    }

    @Test
    void consumerNamesOverrideTest() throws Exception {
        final Consumer consumer = consumerAggregator.getConsumers().stream()
            .filter($ -> $.getConsumerName().equals(TestConsumers.CUSTOM_CONSUMER_NAME) &&
                $.getSubscription().equals(TestConsumers.CUSTOM_SUBSCRIPTION_NAME))
            .findFirst()
            .orElseThrow(() -> new Exception("Missing tested consumer."));

        Assertions.assertEquals(urlBuildService.buildTopicUrl(TestConsumers.CUSTOM_CONSUMER_TOPIC), consumer.getTopic());

        producer.send(TestConsumers.CUSTOM_CONSUMER_TOPIC, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.customConsumerTestReceived.get());
    }

    @Test
    void consumerNamesOverrideWithSpELSupportTest() throws Exception {
        final Consumer consumer = consumerAggregator.getConsumers().stream()
            .filter($ -> $.getConsumerName().equals(customConsumerName) &&
                $.getSubscription().equals(customSubscriptionName))
            .findFirst()
            .orElseThrow(() -> new Exception("Missing tested consumer."));

        Assertions.assertEquals(urlBuildService.buildTopicUrl(TestConsumers.CUSTOM_SUB_AND_CONSUMER_TOPIC), consumer.getTopic());

        producer.send(TestConsumers.CUSTOM_SUB_AND_CONSUMER_TOPIC, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToCustomSpElConsumerAndSubConfig.get());
    }

    @Test
    void sharedSubscriptionOverride() throws Exception {
        final ConsumerBase<?> consumer = (ConsumerBase<?>) consumerAggregator.getConsumers().stream()
            .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.SHARED_SUB_TEST)))
            .findFirst()
            .orElseThrow(() -> new Exception("Missing tested consumer."));

        final Field f = ConsumerBase.class.getDeclaredField("conf");

        f.setAccessible(true);

        final ConsumerConfigurationData<?> conf = (ConsumerConfigurationData<?>) f.get(consumer);

        Assertions.assertEquals(urlBuildService.buildTopicUrl(TestConsumers.SHARED_SUB_TEST), consumer.getTopic());
        Assertions.assertEquals(SubscriptionType.Shared, conf.getSubscriptionType());

        producer.send(TestConsumers.SHARED_SUB_TEST, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToSharedTopicSubscription.get());
    }

    @Test
    void exclusiveSubscriptionOverride() throws Exception {
        final ConsumerBase<?> consumer = (ConsumerBase<?>) consumerAggregator.getConsumers().stream()
            .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.EXCLUSIVE_SUB_TEST)))
            .findFirst()
            .orElseThrow(() -> new Exception("Missing tested consumer."));

        final Field f = ConsumerBase.class.getDeclaredField("conf");

        f.setAccessible(true);

        final ConsumerConfigurationData<?> conf = (ConsumerConfigurationData<?>) f.get(consumer);

        Assertions.assertEquals(urlBuildService.buildTopicUrl(TestConsumers.EXCLUSIVE_SUB_TEST), consumer.getTopic());
        Assertions.assertEquals(SubscriptionType.Exclusive, conf.getSubscriptionType());

        producer.send(TestConsumers.EXCLUSIVE_SUB_TEST, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.subscribeToSharedTopicSubscription.get());
    }

    @Test
    void testFluxConsumer() throws PulsarClientException {
        final AtomicBoolean received = new AtomicBoolean(false);

        myTestFluxConsumer.asSimpleFlux()
            .doOnError(error -> System.out.println(error.getMessage()))
            .subscribe(msg -> {
                Assertions.assertNotNull(msg);
                Assertions.assertEquals(VALIDATION_STRING, msg.getData());
                received.set(true);
            });

        producer.send(TestFluxConsumersConfiguration.BASIC_FLUX_TOPIC_TEST, new MyMsg(VALIDATION_STRING));

        await().atMost(Duration.ofSeconds(10)).until(received::get);
    }

    @Test
    void testRobustFluxConsumer() throws PulsarClientException {
        final AtomicBoolean received = new AtomicBoolean(false);

        robustFluxConsumer.asFlux()
            .doOnError(error -> System.out.println(error.getMessage()))
            .subscribe(msg -> {
                try {
                    final MyMsg myMsg = (MyMsg) msg.getMessage().getValue();

                    Assertions.assertNotNull(myMsg);
                    Assertions.assertEquals(VALIDATION_STRING, myMsg.getData());

                    msg.getConsumer().acknowledge(msg.getMessage());
                    received.set(true);
                } catch (PulsarClientException e) {
                    msg.getConsumer().negativeAcknowledge(msg.getMessage());
                    e.printStackTrace();
                }
            });

        producer.send(TestFluxConsumersConfiguration.ROBUST_FLUX_TOPIC_TEST, new MyMsg(VALIDATION_STRING));

        await().atMost(Duration.ofSeconds(10)).until(received::get);
    }

    @Test
    void testCustomNamespace() throws Exception  {
        final Consumer consumer = consumerAggregator.getConsumers().stream()
                .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.CUSTOM_CONSUMER_TOPIC, "default")))
                .findFirst()
                .orElseThrow(() -> new Exception("Missing tested consumer."));

        producer.send(TestConsumers.CUSTOM_NAMESPACE_TOPIC, new MyMsg(VALIDATION_STRING));
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.customConsumerNamespaceReceived.get());
    }
    
    @Test
    void testBatchConsumerWithAutoAck() throws Exception  {
        final Consumer consumer = consumerAggregator.getConsumers().stream()
                .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_AUTO_ACK, "default")))
                .findFirst()
                .orElseThrow(() -> new Exception("Missing tested consumer."));
        for(int i = 0; i<10;i++) {
            producer.send(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_AUTO_ACK, new MyMsg(VALIDATION_STRING));
        }
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.batchMessageWithAutoAckReceived.get());
        Assertions.assertEquals(consumer.getStats().getNumAcksSent(),10); 
    }
    
    @Test
    void testBatchConsumerWithAckList() throws Exception  {
        final Consumer consumer = consumerAggregator.getConsumers().stream()
                .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_ACK_FROM_LIST, "default")))
                .findFirst()
                .orElseThrow(() -> new Exception("Missing tested consumer."));
        for(int i = 0; i<10;i++) {
            producer.send(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_ACK_FROM_LIST, new MyMsg(VALIDATION_STRING));
        }
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.batchMessageWithAckListReceived.get());
        Assertions.assertEquals(consumer.getStats().getNumAcksSent(),10); 
    }
    
    @Test
    void testBatchConsumerWithManualAck() throws Exception  {
        final Consumer consumer = consumerAggregator.getConsumers().stream()
                .filter($ -> $.getTopic().equals(urlBuildService.buildTopicUrl(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_MANUAL_ACK, "default")))
                .findFirst()
                .orElseThrow(() -> new Exception("Missing tested consumer."));
        for(int i = 0; i<10;i++) {
            producer.send(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_MANUAL_ACK, new MyMsg(VALIDATION_STRING));
        }
        await().atMost(Duration.ofSeconds(10)).until(() -> testConsumers.batchMessageWithManualAckReceived.get());
        Assertions.assertEquals(consumer.getStats().getNumAcksSent(),10); 
    }

    @Test
    void testSubscriptionPropertiesCompressed() throws PulsarClientException {
        producer.createMessage(TestConsumers.SUBSCRIPTION_PROPERTIES, new MyMsg(VALIDATION_STRING))
            .property(TestConsumers.TEST_KEY, TestConsumers.TEST_VALUE)
            .send();

        await().untilTrue(testConsumers.subscriptionPropertiesReceived);
    }
}
