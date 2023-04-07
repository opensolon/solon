package io.github.majusko.pulsar2.solon;

import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.interceptor.ProducerInterceptor;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

import io.github.majusko.pulsar2.solon.consumer.ConsumerAggregator;
import io.github.majusko.pulsar2.solon.consumer.DefaultConsumerInterceptor;
import io.github.majusko.pulsar2.solon.producer.DefaultProducerInterceptor;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;

@Configuration
public class Pulsar2ProducerConfiguration {

    @Bean
    @Condition(onMissingBean = ProducerInterceptor.class)
    public ProducerInterceptor producerInterceptor(){
        return new DefaultProducerInterceptor();
    }
	
}
