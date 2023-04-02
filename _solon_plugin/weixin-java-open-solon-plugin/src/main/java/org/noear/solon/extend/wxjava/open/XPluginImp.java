package org.noear.solon.extend.wxjava.open;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.wxjava.open.config.WxOpenServiceAutoConfiguration;
import org.noear.solon.extend.wxjava.open.config.storage.WxOpenInJedisConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.open.config.storage.WxOpenInMemoryConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.open.config.storage.WxOpenInRedisConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.open.config.storage.WxOpenInRedisTemplateConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.open.config.storage.WxOpenInRedissonConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.open.properties.WxOpenProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxOpenProperties.class);
            context.beanMake(WxOpenInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxOpenInRedissonConfigStorageConfiguration.class);
            context.beanMake(WxOpenInRedisConfigStorageConfiguration.class);
            context.beanMake(WxOpenInJedisConfigStorageConfiguration.class);
            context.beanMake(WxOpenInRedisTemplateConfigStorageConfiguration.class);
            context.beanMake(WxOpenServiceAutoConfiguration.class);
        });
	}

}
