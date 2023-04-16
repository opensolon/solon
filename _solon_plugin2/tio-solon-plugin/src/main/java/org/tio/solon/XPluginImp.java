package org.tio.solon;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.event.AppBeanLoadEndEvent;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventBus;
import org.tio.core.intf.GroupListener;
import org.tio.core.stat.IpStatListener;
import org.tio.server.TioServerConfig;
import org.tio.server.intf.TioServerHandler;
import org.tio.server.intf.TioServerListener;
import org.tio.solon.annotation.TioServerGroupListenerAnno;
import org.tio.solon.annotation.TioServerHandlerAnno;
import org.tio.solon.annotation.TioServerIpStatListenerAnno;
import org.tio.solon.annotation.TioServerListenerAnno;
import org.tio.solon.config.TioServerProperties;
import org.tio.solon.config.TioServerSslProperties;
import org.tio.solon.integration.TioServerBootstrap;

public class XPluginImp implements Plugin {

	private TioServerBootstrap tb;

	@Override
	public void start(AopContext context) throws Throwable {
		TioServerHandlerBeanBuilder tmb = new TioServerHandlerBeanBuilder();
		context.beanBuilderAdd(TioServerHandlerAnno.class, tmb);

		TioServerListenerBeanBuilder tsb = new TioServerListenerBeanBuilder();
		context.beanBuilderAdd(TioServerListenerAnno.class, tsb);

		TioServerIpStatListenerBeanBuilder tib = new TioServerIpStatListenerBeanBuilder();
		context.beanBuilderAdd(TioServerIpStatListenerAnno.class, tib);

		TioServerGroupListenerBeanBuilder tgb = new TioServerGroupListenerBeanBuilder();
		context.beanBuilderAdd(TioServerGroupListenerAnno.class, tgb);
		
		Props props = context.cfg();

		TioServerProperties tsproperties = props.getBean("tio.core.server", TioServerProperties.class);
		TioServerSslProperties tsslproperties = props.getBean("tio.core.ssl", TioServerSslProperties.class);

//		// 晚点启动，让扫描时产生的组件可以注册进来
		EventBus.subscribe(AppBeanLoadEndEvent.class, e -> {
			TioServerHandler tsh = TioServerBeanSet.shandlers.get(0);
			TioServerListener tsl = TioServerBeanSet.slisteners.get(0);
			IpStatListener tsi = null;
			if(TioServerBeanSet.ilisteners.size()>0) {
				tsi = TioServerBeanSet.ilisteners.get(0);				
			}
			GroupListener tsg = null;
			if(TioServerBeanSet.glisteners.size()>0) {
				tsg = TioServerBeanSet.glisteners.get(0);
			}
			tb = new TioServerBootstrap(tsproperties, tsslproperties, tsi, tsg, tsh, tsl);
			tb.contextInitialized();
			
			e.context().wrapAndPut(TioServerConfig.class, tb.getServerTioConfig());
			
			TioServerBeanSet.clear();
		});

		//应用加载完后，再启动tio
        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
        	tb.start();
        });
	}

	@Override
	public void stop() throws Throwable {
		if (tb != null) {
			tb.stop();
		}
	}
}
