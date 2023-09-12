package org.noear.solon.socketd.integration;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.socketd.RouterListener;
import org.noear.solon.socketd.annotation.ClientEndpoint;

public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) {
        //注册 @ClientListenEndpoint 构建器

        Solon.app().listenAfter(new RouterListener());

        context.beanBuilderAdd(ClientEndpoint.class, new ClientEndpointBeanBuilder());
    }
}
