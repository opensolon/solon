package cn.binarywang.wx.miniapp.solon.config.storage;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import cn.binarywang.wx.miniapp.solon.properties.WxMaProperties;

import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.miniapp.config-storage.type:memory}=memory")
public class WxMaInMemoryConfigStorageConfiguration extends AbstractWxMaConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxMaConfig.class)
	public WxMaConfig wxMaConfig(WxMaProperties properties) {
		WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
		return this.config(config, properties);
	}
}
