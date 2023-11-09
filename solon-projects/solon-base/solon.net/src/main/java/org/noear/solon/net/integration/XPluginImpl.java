package org.noear.solon.net.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.net.annotation.ClientEndpoint;
import org.noear.solon.net.annotation.ServerEndpoint;

/**
 * @author noear
 * @since 2.6
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        context.beanBuilderAdd(ServerEndpoint.class, ((clz, bw, anno) -> {

        }));

        context.beanBuilderAdd(ClientEndpoint.class, ((clz, bw, anno) -> {

        }));
    }
}
