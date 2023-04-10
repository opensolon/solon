package me.chanjar.weixin.mp.solon.config.storage;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.mp.solon.properties.WxMpProperties;

import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.mp.config-storage.type:memory}=memory")
public class WxMpInMemoryConfigStorageConfiguration extends AbstractWxMpConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxMpConfigStorage.class)
	public WxMpConfigStorage wxMaConfig(WxMpProperties properties) {
		WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
		return this.config(config, properties);
	}
}
