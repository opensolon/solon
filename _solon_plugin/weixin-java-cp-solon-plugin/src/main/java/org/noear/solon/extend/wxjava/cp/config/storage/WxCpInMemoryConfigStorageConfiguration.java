package org.noear.solon.extend.wxjava.cp.config.storage;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.extend.wxjava.cp.properties.WxCpProperties;

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
