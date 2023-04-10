package me.chanjar.weixin.qidian.solon;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import me.chanjar.weixin.qidian.solon.config.WxQidianServiceAutoConfiguration;
import me.chanjar.weixin.qidian.solon.config.storage.WxQidianInJedisConfigStorageConfiguration;
import me.chanjar.weixin.qidian.solon.config.storage.WxQidianInMemoryConfigStorageConfiguration;
import me.chanjar.weixin.qidian.solon.config.storage.WxQidianInRedisTemplateConfigStorageConfiguration;
import me.chanjar.weixin.qidian.solon.config.storage.WxQidianInRedissonConfigStorageConfiguration;
import me.chanjar.weixin.qidian.solon.properties.WxQidianProperties;

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
