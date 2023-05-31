package org.noear.solon.admin.server.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.message.Session;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public List<Session> initialSessions(
            @Inject(required = false) AdminServerBootstrapConfiguration.MarkedServerEnabled marked
    ) {
        if (marked == null) return null;

        return new ArrayList<>();
    }

}
