package me.chanjar.weixin.open.solon.config.storage;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.open.solon.properties.WxOpenProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import me.chanjar.weixin.open.api.WxOpenConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisTemplateConfigStorage;

/**
 * @author yl
 * 不建议使用Spring系列，请使用redisson
 */
@Deprecated
@Configuration
@Condition(onProperty = "${wx.open.config-storage.type}=redistemplate",onClass = StringRedisTemplate.class)
public class WxOpenInRedisTemplateConfigStorageConfiguration extends AbstractWxOpenConfigStorageConfiguration {

  @Bean
  @Condition(onMissingBean =  WxOpenConfigStorage.class)
  public WxOpenConfigStorage wxOpenConfigStorage(WxOpenProperties properties) {
    WxOpenInMemoryConfigStorage config = getWxOpenInRedisTemplateConfigStorage(properties);
    return this.config(config, properties);
  }

  private WxOpenInRedisTemplateConfigStorage getWxOpenInRedisTemplateConfigStorage(WxOpenProperties properties) {
    StringRedisTemplate redisTemplate = Solon.context().getBean(StringRedisTemplate.class);
    return new WxOpenInRedisTemplateConfigStorage(redisTemplate, properties.getConfigStorage().getKeyPrefix());
  }
}
