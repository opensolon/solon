package me.chanjar.weixin.cp.solon.config.storage;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.cp.solon.properties.WxCpProperties;

import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.cp.config-storage.type:memory}=memory")
public class WxCpInMemoryConfigStorageConfiguration extends AbstractWxCpConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxCpConfigStorage.class)
	public WxCpConfigStorage wxMaConfig(WxCpProperties properties) {
		WxCpDefaultConfigImpl config = new WxCpDefaultConfigImpl();
		return this.config(config, properties);
	}
}
