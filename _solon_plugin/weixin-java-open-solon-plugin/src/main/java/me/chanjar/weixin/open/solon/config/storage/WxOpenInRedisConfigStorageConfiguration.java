package me.chanjar.weixin.open.solon.config.storage;

import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.open.api.WxOpenConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisConfigStorage;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.open.solon.properties.RedisProperties;
import me.chanjar.weixin.open.solon.properties.WxOpenProperties;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author yl
 * 不建议使用Jedis
 */
@Deprecated
@Configuration
@Condition(onProperty = "${wx.open.config-storage.type}=redis",onClass = JedisPool.class)
public class WxOpenInRedisConfigStorageConfiguration extends AbstractWxOpenConfigStorageConfiguration {

  @Bean
  @Condition(onMissingBean=WxOpenConfigStorage.class)
  public WxOpenConfigStorage wxOpenConfigStorage(WxOpenProperties properties) {
    WxOpenInMemoryConfigStorage config = getWxOpenInRedisConfigStorage(properties);
    return this.config(config, properties);
  }

  private WxOpenInRedisConfigStorage getWxOpenInRedisConfigStorage(final WxOpenProperties properties) {
    RedisProperties redisProperties = properties.getConfigStorage().getRedis();
    JedisPool jedisPool;
    if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
      jedisPool = getJedisPool(properties);
    } else {
      jedisPool = Solon.context().getBean(JedisPool.class);
    }
    WxRedisOps redisOps = new JedisWxRedisOps(jedisPool);
    return new WxOpenInRedisConfigStorage(redisOps, properties.getConfigStorage().getKeyPrefix());
  }

  private JedisPool getJedisPool(final WxOpenProperties properties) {
    WxOpenProperties.ConfigStorage storage = properties.getConfigStorage();
    RedisProperties redis = storage.getRedis();

    JedisPoolConfig config = new JedisPoolConfig();
    if (redis.getMaxActive() != null) {
      config.setMaxTotal(redis.getMaxActive());
    }
    if (redis.getMaxIdle() != null) {
      config.setMaxIdle(redis.getMaxIdle());
    }
    if (redis.getMaxWaitMillis() != null) {
      config.setMaxWaitMillis(redis.getMaxWaitMillis());
    }
    if (redis.getMinIdle() != null) {
      config.setMinIdle(redis.getMinIdle());
    }
    config.setTestOnBorrow(true);
    config.setTestWhileIdle(true);

    return new JedisPool(config, redis.getHost(), redis.getPort(),
      redis.getTimeout(), redis.getPassword(), redis.getDatabase());
  }
}
