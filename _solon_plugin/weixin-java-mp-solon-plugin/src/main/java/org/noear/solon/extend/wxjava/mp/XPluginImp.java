package org.noear.solon.extend.wxjava.mp;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.wxjava.mp.config.WxMpServiceAutoConfiguration;
import org.noear.solon.extend.wxjava.mp.config.storage.WxMpInJedisConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.mp.config.storage.WxMpInMemoryConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.mp.config.storage.WxMpInRedisTemplateConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.mp.config.storage.WxMpInRedissonConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.mp.properties.WxMpProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxMpProperties.class);
            context.beanMake(WxMpInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxMpInRedissonConfigStorageConfiguration.class);
            context.beanMake(WxMpInJedisConfigStorageConfiguration.class);
            context.beanMake(WxMpInRedisTemplateConfigStorageConfiguration.class);
            context.beanMake(WxMpServiceAutoConfiguration.class);
        });
	}

}
