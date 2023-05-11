package org.noear.solon.admin.client.config;

import lombok.var;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cloud.annotation.CloudConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AdminClientBootstrapConfiguration {

    private static Logger logger = LoggerFactory.getLogger(AdminClientBootstrapConfiguration.class);

    @Condition(onProperty = "${solon.admin.client.serverUrl}")
    @Bean
    public IClientProperties localClientProperties(@Inject(value = "${solon.admin.client}") LocalClientProperties properties) {
        logger.debug("Injected localClientProperties: " + properties);
        return properties;
    }


    @Condition(onProperty = "${solon.cloud}")
    @Bean
    public IClientProperties cloudClientProperties(@CloudConfig("solon.admin.client") CloudClientProperties properties) {
        logger.debug("Injected cloudClientProperties: " + properties);
        return properties;
    }

    @Bean
    public MarkedClientEnabled markedClientEnabled() {
        var clientProperties = Solon.context().getBean(IClientProperties.class);
        if (clientProperties == null || !clientProperties.isEnabled()) {
            logger.error("Failed to enable Solon Admin client.", new IllegalStateException("Could not enable Solon Admin client because none of the properties has been configured correctly."));
            return null;
        }
        return new MarkedClientEnabled();
    }

    public static class MarkedClientEnabled {
    }

}
