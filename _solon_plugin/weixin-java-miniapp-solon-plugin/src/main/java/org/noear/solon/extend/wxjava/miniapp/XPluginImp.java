package org.noear.solon.extend.wxjava.miniapp;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.wxjava.miniapp.config.WxMaServiceAutoConfiguration;
import org.noear.solon.extend.wxjava.miniapp.config.storage.WxMaInJedisConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.miniapp.config.storage.WxMaInMemoryConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.miniapp.config.storage.WxMaInRedisTemplateConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.miniapp.config.storage.WxMaInRedissonConfigStorageConfiguration;
import org.noear.solon.extend.wxjava.miniapp.properties.WxMaProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AopContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxMaProperties.class);
            
            context.beanMake(WxMaInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxMaInRedissonConfigStorageConfiguration.class); 
            context.beanMake(WxMaInJedisConfigStorageConfiguration.class); //@Deprecated
            context.beanMake(WxMaInRedisTemplateConfigStorageConfiguration.class); //@Deprecated
            
            context.beanMake(WxMaServiceAutoConfiguration.class);
        });
	}

}
