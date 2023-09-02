package cn.binarywang.wx.miniapp.solon;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import cn.binarywang.wx.miniapp.solon.config.WxMaServiceAutoConfiguration;
import cn.binarywang.wx.miniapp.solon.config.storage.WxMaInJedisConfigStorageConfiguration;
import cn.binarywang.wx.miniapp.solon.config.storage.WxMaInMemoryConfigStorageConfiguration;
import cn.binarywang.wx.miniapp.solon.config.storage.WxMaInRedissonConfigStorageConfiguration;
import cn.binarywang.wx.miniapp.solon.properties.WxMaProperties;

public class XPluginImp implements Plugin{

	@Override
	public void start(AppContext context) throws Throwable {
		context.lifecycle(-99, () -> {
            context.beanMake(WxMaProperties.class);
            
            context.beanMake(WxMaInMemoryConfigStorageConfiguration.class);
            context.beanMake(WxMaInRedissonConfigStorageConfiguration.class); 
            context.beanMake(WxMaInJedisConfigStorageConfiguration.class); //@Deprecated
            
            context.beanMake(WxMaServiceAutoConfiguration.class);
        });
	}

}
