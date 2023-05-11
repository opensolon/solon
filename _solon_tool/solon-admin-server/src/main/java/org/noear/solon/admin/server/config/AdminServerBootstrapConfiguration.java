package org.noear.solon.admin.server.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

@Condition(onProperty = "${solon.admin.server.enabled} = true")
@Configuration
public class AdminServerBootstrapConfiguration {

    @Bean
    public ServerProperties serverProperties() {
        return new ServerProperties();
    }

    @Bean
    public MarkedServerEnabled markedServerEnabled() {
        return new MarkedServerEnabled();
    }

    public static class MarkedServerEnabled {
    }

}
