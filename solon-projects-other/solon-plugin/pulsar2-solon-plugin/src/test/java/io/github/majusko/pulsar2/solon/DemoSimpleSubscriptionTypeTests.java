package io.github.majusko.pulsar2.solon;

import io.github.majusko.pulsar.TestConsumers;
import io.github.majusko.pulsar.TestProducerConfiguration;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar2.solon.consumer.ConsumerAggregator;
import io.github.majusko.pulsar2.solon.producer.PulsarTemplate;
import io.github.majusko.pulsar2.solon.utils.UrlBuildService;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.ConsumerBase;
import org.apache.pulsar.client.impl.conf.ConsumerConfigurationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;


import java.lang.reflect.Field;
import java.time.Duration;

import static org.awaitility.Awaitility.await;

@Import({TestProducerConfiguration.class, TestConsumers.class})
public class DemoSimpleSubscriptionTypeTests {

    @Inject
    private ConsumerAggregator consumerAggregator;

    @Inject
    private PulsarTemplate<MyMsg> producer;

    @Inject
    private TestConsumers testConsumers;

    @Inject
    private UrlBuildService urlBuildService;

    public static final String VALIDATION_STRING = "validation-string";

//    @DynamicPropertySource
//    static void propertySettings(DynamicPropertyRegistry registry) {
//        registry.add("pulsar.serviceUrl", pulsarContainer::getPulsarBrokerUrl);
//    }

    @Test
    void testProducerSendMethod() throws PulsarClientException {
        producer.send("topic-one", new MyMsg("bb"));

        await().untilTrue(testConsumers.mockTopicListenerReceived);
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
}
