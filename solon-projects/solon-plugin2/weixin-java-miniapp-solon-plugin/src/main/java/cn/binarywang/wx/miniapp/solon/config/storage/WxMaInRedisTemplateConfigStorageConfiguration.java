package cn.binarywang.wx.miniapp.solon.config.storage;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import cn.binarywang.wx.miniapp.solon.properties.WxMaProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;

/**
 * @author yl TaoYu
 */
@Deprecated
@Configuration
@Condition(onProperty = "${wx.miniapp.config-storage.type}=redistemplate", onClass = StringRedisTemplate.class)
public class WxMaInRedisTemplateConfigStorageConfiguration extends AbstractWxMaConfigStorageConfiguration {

  @Bean
  @Condition(onMissingBean = WxMaConfig.class)
  public WxMaConfig wxMaConfig(WxMaProperties properties) {
    WxMaRedisBetterConfigImpl config = getWxMaInRedisTemplateConfigStorage(properties);
    return this.config(config, properties);
  }

  private WxMaRedisBetterConfigImpl getWxMaInRedisTemplateConfigStorage(final WxMaProperties properties) {
    StringRedisTemplate redisTemplate = Solon.context().getBean(StringRedisTemplate.class);
    WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
    return new WxMaRedisBetterConfigImpl(redisOps, properties.getConfigStorage().getKeyPrefix());
  }
}
