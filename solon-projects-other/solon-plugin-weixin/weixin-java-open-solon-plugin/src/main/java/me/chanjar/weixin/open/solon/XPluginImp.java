package me.chanjar.weixin.open.solon;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import me.chanjar.weixin.open.solon.config.WxOpenServiceAutoConfiguration;
import me.chanjar.weixin.open.solon.config.storage.WxOpenInJedisConfigStorageConfiguration;
import me.chanjar.weixin.open.solon.config.storage.WxOpenInMemoryConfigStorageConfiguration;
import me.chanjar.weixin.open.solon.config.storage.WxOpenInRedisConfigStorageConfiguration;
import me.chanjar.weixin.open.solon.config.storage.WxOpenInRedissonConfigStorageConfiguration;
import me.chanjar.weixin.open.solon.properties.WxOpenProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AppContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxOpenProperties.class);
            context.beanMake(WxOpenInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxOpenInRedissonConfigStorageConfiguration.class);
            context.beanMake(WxOpenInRedisConfigStorageConfiguration.class);
            context.beanMake(WxOpenInJedisConfigStorageConfiguration.class);
            context.beanMake(WxOpenServiceAutoConfiguration.class);
        });
	}

}
