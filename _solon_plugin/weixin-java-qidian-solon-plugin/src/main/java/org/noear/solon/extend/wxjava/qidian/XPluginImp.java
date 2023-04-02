package org.noear.solon.extend.wxjava.qidian;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.wxjava.qidian.config.WxQidianServiceAutoConfiguration;
import org.noear.solon.extend.wxjava.qidian.config.storage.WxQidianInJedisConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.qidian.config.storage.WxQidianInMemoryConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.qidian.config.storage.WxQidianInRedisTemplateConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.qidian.config.storage.WxQidianInRedissonConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.qidian.properties.WxQidianProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxQidianProperties.class);
            context.beanMake(WxQidianInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxQidianInRedissonConfigStorageConfiguration.class);
            context.beanMake(WxQidianInJedisConfigStorageConfiguration.class);
            context.beanMake(WxQidianInRedisTemplateConfigStorageConfiguration.class);
            context.beanMake(WxQidianServiceAutoConfiguration.class);
        });
	}

}
