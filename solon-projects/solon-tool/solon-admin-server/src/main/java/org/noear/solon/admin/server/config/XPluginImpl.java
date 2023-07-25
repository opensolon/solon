package org.noear.solon.admin.server.config;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author shaokeyibb
 * @since 2.3
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        Solon.app().enableWebSocket(true);
        Solon.app().enableWebSocketMvc(false);
    }
}
