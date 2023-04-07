package io.github.majusko.pulsar;

import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar2.solon.error.exception.ClientInitException;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumer;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumerFactory;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumerHolder;
import io.github.majusko.pulsar2.solon.reactor.PulsarFluxConsumer;

@Configuration
public class TestFluxConsumersConfiguration {

    public static final String BASIC_FLUX_TOPIC_TEST = "basic-flux-test-topic";
    public static final String ROBUST_FLUX_TOPIC_TEST = "robust-flux-test-topic";

    @Inject
    private FluxConsumerFactory fluxConsumerFactory;

    @Bean
    public FluxConsumer<MyMsg> myTestFluxConsumer() throws ClientInitException, PulsarClientException {
        return fluxConsumerFactory.newConsumer(
            PulsarFluxConsumer.builder()
                .setTopic(BASIC_FLUX_TOPIC_TEST)
                .setConsumerName("my-consumer-name")
                .setSubscriptionName("my-subscription-name")
                .setMessageClass(MyMsg.class)
                .setInitialPosition(SubscriptionInitialPosition.Latest)
                .build());
    }

    @Bean
    public FluxConsumer<FluxConsumerHolder> robustFluxConsumer() throws ClientInitException, PulsarClientException {
        return fluxConsumerFactory.newConsumer(
            PulsarFluxConsumer.builder()
                .setTopic(ROBUST_FLUX_TOPIC_TEST)
                .setConsumerName("my-robust-consumer-name")
                .setSubscriptionName("my-robust-subscription-name")
                .setMessageClass(MyMsg.class)
                .setBackPressureBufferSize(1024)
                .setSimple(false)
                .setInitialPosition(SubscriptionInitialPosition.Latest)
                .build());
    }
}
