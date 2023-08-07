package io.github.majusko.pulsar2.solon;

import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.PulsarClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

import io.github.majusko.pulsar2.solon.consumer.ConsumerAggregator;
import io.github.majusko.pulsar2.solon.consumer.DefaultConsumerInterceptor;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;

@Configuration
public class Pulsar2ConsumerConfiguration {

    @Bean
    @Condition(onMissingBean = ConsumerInterceptor.class)
    public ConsumerInterceptor consumerInterceptor(){
        return new DefaultConsumerInterceptor();
    }
	
//	@Bean
//	@Condition(onMissingBean = ConsumerAggregator.class)
//	ConsumerAggregator getConsumerAggregator(PulsarClient pulsarClient,
//            ConsumerProperties consumerProperties, PulsarProperties pulsarProperties, 
//            ConsumerInterceptor consumerInterceptor) {
//		ConsumerAggregator ca = new ConsumerAggregator(pulsarClient,consumerProperties,pulsarProperties,consumerInterceptor);
//		return ca;
//	}
	
}
