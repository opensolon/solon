package org.noear.solon.admin.client.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cloud.annotation.CloudConfig;

@Configuration
public class AdminClientBootstrapConfiguration {

    @Condition(onProperty = "${solon.admin.client.enabled} = true")
    @Bean
    public LocalClientProperties localClientProperties(@Inject("${solon.admin.client}") LocalClientProperties properties) {
        return properties;
    }

    @Condition(onMissingBean = LocalClientProperties.class)
    @Bean
    public CloudClientProperties cloudClientProperties(@CloudConfig("solon.admin.client") CloudClientProperties properties) {
        return properties;
    }

    @Bean
    public MarkedClientEnabled markedClientEnabled() {
        return new MarkedClientEnabled();
    }

    public static class MarkedClientEnabled {
    }

}
