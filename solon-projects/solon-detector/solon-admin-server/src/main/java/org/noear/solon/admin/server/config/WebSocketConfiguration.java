package org.noear.solon.admin.server.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.message.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 初始化
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Configuration
public class WebSocketConfiguration {

    @Bean(name = "applicationWebsocketSessions")
    public List<Session> initialSessions() {
        return new ArrayList<>();
    }
}
