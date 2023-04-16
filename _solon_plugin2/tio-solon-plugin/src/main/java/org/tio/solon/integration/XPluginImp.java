package org.tio.solon.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.server.TioServerConfig;
import org.tio.server.intf.TioServerHandler;
import org.tio.server.intf.TioServerListener;
import org.tio.solon.config.TioServerProperties;
import org.tio.solon.config.TioServerSslProperties;

public class XPluginImp implements Plugin {

    private TioServerBootstrap server;

    @Override
    public void start(AopContext context) throws Throwable {
        TioServerProperties tsproperties = context.cfg().getBean("tio.core.server", TioServerProperties.class);
        TioServerSslProperties tsslproperties = context.cfg().getBean("tio.core.ssl", TioServerSslProperties.class);

        // 晚点启动，让扫描时产生的组件可以注册进来
        EventBus.subscribe(AppBeanLoadEndEvent.class, e -> {
            TioServerHandler tsh = context.getBean(TioServerHandler.class);
            TioServerListener tsl = context.getBean(TioServerListener.class);
            IpStatListener tsi = context.getBean(IpStatListener.class);

            GroupListener tsg = context.getBean(GroupListener.class);

            server = new TioServerBootstrap(tsproperties, tsslproperties, tsi, tsg, tsh, tsl);
            server.contextInitialized();

            context.wrapAndPut(TioServerConfig.class, server.getServerTioConfig());
        });

        //应用加载完后，再启动tio
        EventBus.subscribe(AppLoadEndEvent.class, e -> {
            server.start();
        });
    }

    @Override
    public void stop() throws Throwable {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}
