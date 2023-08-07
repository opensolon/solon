package com.github.binarywang.wxpay.solon;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import com.github.binarywang.wxpay.solon.config.WxPayAutoConfiguration;
import com.github.binarywang.wxpay.solon.properties.WxPayProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxPayProperties.class);
            context.beanMake(WxPayAutoConfiguration.class);
        });
	}

}
