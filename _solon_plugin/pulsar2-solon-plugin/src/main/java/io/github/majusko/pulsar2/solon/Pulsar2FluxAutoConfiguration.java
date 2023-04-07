package io.github.majusko.pulsar2.solon;

import org.apache.pulsar.client.api.ConsumerInterceptor;
import org.apache.pulsar.client.api.PulsarClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;
import io.github.majusko.pulsar2.solon.reactor.FluxConsumerFactory;
import io.github.majusko.pulsar2.solon.utils.UrlBuildService;

@Configuration
public class Pulsar2FluxAutoConfiguration {

	@Bean
	@Condition(onMissingBean = FluxConsumerFactory.class)
	public FluxConsumerFactory getFluxConsumerFactory(PulsarClient pulsarClient, ConsumerProperties consumerProperties, PulsarProperties pulsarProperties, ConsumerInterceptor consumerInterceptor) {
		UrlBuildService urlBuildService = new UrlBuildService(pulsarProperties, consumerProperties);
		return new FluxConsumerFactory(pulsarClient,urlBuildService,consumerProperties,pulsarProperties,consumerInterceptor);
	}
	
}
