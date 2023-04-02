package org.noear.solon.extend.wxjava.pay;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.wxjava.pay.config.WxPayAutoConfiguration;
import org.noear.solon.extend.wxjava.pay.properties.WxPayProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxPayProperties.class);
            context.beanMake(WxPayAutoConfiguration.class);
        });
	}

}
