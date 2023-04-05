package me.chanjar.weixin.open.solon.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.open.solon.properties.RedisProperties;
import me.chanjar.weixin.open.solon.properties.WxOpenProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import me.chanjar.weixin.common.redis.RedissonWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.open.api.WxOpenConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;


/**
 * @author yl
 */
@Configuration
@Condition(onProperty = "${wx.open.config-storage.type}=redisson",onClass = RedissonClient.class)
public class WxOpenInRedissonConfigStorageConfiguration extends AbstractWxOpenConfigStorageConfiguration {

  @Bean
  @Condition(onMissingBean =  WxOpenConfigStorage.class)
  public WxOpenConfigStorage wxOpenConfigStorage(WxOpenProperties properties) {
    WxOpenInMemoryConfigStorage config = getWxOpenInRedissonConfigStorage(properties);
    return this.config(config, properties);
  }

  private WxOpenInRedisConfigStorage getWxOpenInRedissonConfigStorage(final WxOpenProperties properties) {
    RedisProperties redisProperties = properties.getConfigStorage().getRedis();
    RedissonClient redissonClient;
    if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
      redissonClient = getRedissonClient(properties);
    } else {
      redissonClient = Solon.context().getBean(RedissonClient.class);
    }
    WxRedisOps redisOps = new RedissonWxRedisOps(redissonClient);
    return new WxOpenInRedisConfigStorage(redisOps, properties.getConfigStorage().getKeyPrefix());
  }

  private RedissonClient getRedissonClient(WxOpenProperties properties) {
    WxOpenProperties.ConfigStorage storage = properties.getConfigStorage();
    RedisProperties redis = storage.getRedis();

    Config config = new Config();
    config.useSingleServer()
      .setAddress("redis://" + redis.getHost() + ":" + redis.getPort())
      .setDatabase(redis.getDatabase())
      .setPassword(redis.getPassword());
    config.setTransportMode(TransportMode.NIO);
    return Redisson.create(config);
  }
}
