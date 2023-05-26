package io.github.majusko.pulsar;

import org.apache.pulsar.client.api.CompressionType;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

import io.github.majusko.pulsar.msg.AvroMsg;
import io.github.majusko.pulsar.msg.MyMsg;
import io.github.majusko.pulsar.msg.MyMsg2;
import io.github.majusko.pulsar.msg.ProtoMsg;
import io.github.majusko.pulsar2.solon.constant.Serialization;
import io.github.majusko.pulsar2.solon.producer.ProducerFactory;
import io.github.majusko.pulsar2.solon.producer.ProducerMaker;

@Configuration
public class TestProducerConfiguration {

    @Bean
    public ProducerFactory producerFactory() {
        return new ProducerFactory()
            .addProducer("topic-for-error", String.class)
            .addProducer("topic-for-error-2", String.class)
            .addProducer("topic-one", MyMsg.class)
            .addProducer("topic-two", MyMsg2.class, Serialization.JSON)
            .addProducer("topic-avro", AvroMsg.class, Serialization.AVRO)
            .addProducer("topic-async", MyMsg.class)
            .addProducer("topic-message", MyMsg.class)
            .addProducer("topic-retry", MyMsg.class)
            .addProducer("topic-string", String.class, Serialization.STRING)
            .addProducer("topic-byte")
            .addProducer("topic-proto", ProtoMsg.class, Serialization.PROTOBUF)
            .addProducer("topic-deliver-to-dead-letter", MyMsg.class)
            .addProducer("${my.custom.topic.name}", MyMsg.class)
            .addProducer(TestConsumers.CUSTOM_NAMESPACE_TOPIC, "default", MyMsg.class)
            .addProducer(TestConsumers.CUSTOM_SUB_AND_CONSUMER_TOPIC, MyMsg.class)
            .addProducer(TestConsumers.SHARED_SUB_TEST, MyMsg.class)
            .addProducer(TestConsumers.EXCLUSIVE_SUB_TEST, MyMsg.class)
            .addProducer(TestConsumers.CUSTOM_CONSUMER_TOPIC, MyMsg.class)
            .addProducer(TestFluxConsumersConfiguration.BASIC_FLUX_TOPIC_TEST, MyMsg.class)
            .addProducer(TestFluxConsumersConfiguration.ROBUST_FLUX_TOPIC_TEST, MyMsg.class)
            .addProducer(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_AUTO_ACK, MyMsg.class)
            .addProducer(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_ACK_FROM_LIST, MyMsg.class)
            .addProducer(TestConsumers.CUSTOM_BATCH_CONSUMER_TOPIC_MANUAL_ACK, MyMsg.class)
            .addProducer(new ProducerMaker(TestConsumers.SUBSCRIPTION_PROPERTIES, MyMsg.class).setCompressionType(CompressionType.ZLIB));
    }
}
