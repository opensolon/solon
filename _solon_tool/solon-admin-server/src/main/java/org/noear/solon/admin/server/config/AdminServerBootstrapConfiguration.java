package org.noear.solon.admin.server.config;

import lombok.Value;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

@Configuration
public class AdminServerBootstrapConfiguration {

    @Condition(onProperty = "${solon.admin.server.enabled:true} = true")
    @Bean
    public MarkedServerEnabled markedServerEnabled() {
        return new MarkedServerEnabled(Solon.cfg().get("solon.admin.server.mode", "local"));
    }

    @Value
    public static class MarkedServerEnabled {
        String mode;
    }

}
