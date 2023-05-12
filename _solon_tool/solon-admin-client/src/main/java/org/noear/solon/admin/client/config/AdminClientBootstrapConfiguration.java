package org.noear.solon.admin.client.config;

import lombok.Value;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AdminClientBootstrapConfiguration {

    private static Logger logger = LoggerFactory.getLogger(AdminClientBootstrapConfiguration.class);

    @Condition(onProperty = "${solon.admin.client.mode:local} = local")
    @Bean
    public IClientProperties localClientProperties(@Inject(value = "${solon.admin.client}") LocalClientProperties properties) {
        logger.debug("Injected localClientProperties: " + properties);
        return properties;
    }


    @Condition(onProperty = "${solon.admin.client.mode:local} = cloud")
    @Bean
    public IClientProperties cloudClientProperties(@Inject(value = "${solon.admin.client}") CloudClientProperties properties) {
        logger.debug("Injected cloudClientProperties: " + properties);
        return properties;
    }

    @Bean
    public MarkedClientEnabled markedClientEnabled(@Inject(required = false) IClientProperties clientProperties) {
        if (clientProperties == null || !clientProperties.isEnabled()) {
            logger.error("Failed to enable Solon Admin client.", new IllegalStateException("Could not enable Solon Admin client because none of the properties has been configured correctly."));
            return null;
        }
        return new MarkedClientEnabled(clientProperties.getMode());
    }

    @Value
    public static class MarkedClientEnabled {
        String mode;
    }

}
