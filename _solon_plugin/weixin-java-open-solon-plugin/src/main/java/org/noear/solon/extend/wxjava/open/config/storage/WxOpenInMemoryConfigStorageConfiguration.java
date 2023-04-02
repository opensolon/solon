package org.noear.solon.extend.wxjava.open.config.storage;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.extend.wxjava.open.properties.WxOpenProperties;

import me.chanjar.weixin.open.api.WxOpenConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;

/**
 * @author yl
 */
@Configuration
@Condition(onProperty = "${wx.open.config-storage.type:memory}=memory")
public class WxOpenInMemoryConfigStorageConfiguration extends AbstractWxOpenConfigStorageConfiguration {

  @Bean
  @Condition(onMissingBean = WxOpenConfigStorage.class)
  public WxOpenConfigStorage wxOpenConfigStorage(WxOpenProperties properties) {
    WxOpenInMemoryConfigStorage config = new WxOpenInMemoryConfigStorage();
    return this.config(config, properties);
  }
}
