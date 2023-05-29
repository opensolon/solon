package me.chanjar.weixin.qidian.solon.config.storage;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.qidian.solon.properties.WxQidianProperties;

import me.chanjar.weixin.qidian.config.WxQidianConfigStorage;
import me.chanjar.weixin.qidian.config.impl.WxQidianDefaultConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.qidian.config-storage.type:memory}=memory")
public class WxQidianInMemoryConfigStorageConfiguration extends AbstractWxQidianConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxQidianConfigStorage.class)
	public WxQidianConfigStorage wxMaConfig(WxQidianProperties properties) {
		WxQidianDefaultConfigImpl config = new WxQidianDefaultConfigImpl();
		return this.config(config, properties);
	}
}
