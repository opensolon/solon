package org.noear.solon.extend.wxjava.cp;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.wxjava.cp.config.WxCpServiceAutoConfiguration;
import org.noear.solon.extend.wxjava.cp.config.storage.WxCpInMemoryConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.cp.config.storage.WxCpInRedissonConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.cp.properties.WxCpProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxCpProperties.class);
            context.beanMake(WxCpInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxCpInRedissonConfigStorageConfiguration.class);
            context.beanMake(WxCpServiceAutoConfiguration.class);
        });
	}

}
