package me.chanjar.weixin.mp.solon;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import me.chanjar.weixin.mp.solon.config.WxMpServiceAutoConfiguration;
import me.chanjar.weixin.mp.solon.config.storage.WxMpInJedisConfigStorageConfiguration;
import me.chanjar.weixin.mp.solon.config.storage.WxMpInMemoryConfigStorageConfiguration;
import me.chanjar.weixin.mp.solon.config.storage.WxMpInRedisTemplateConfigStorageConfiguration;
import me.chanjar.weixin.mp.solon.config.storage.WxMpInRedissonConfigStorageConfiguration;
import me.chanjar.weixin.mp.solon.properties.WxMpProperties;

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
