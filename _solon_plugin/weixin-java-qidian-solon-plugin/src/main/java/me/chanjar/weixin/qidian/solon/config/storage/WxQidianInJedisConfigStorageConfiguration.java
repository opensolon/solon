package me.chanjar.weixin.qidian.solon.config.storage;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.qidian.solon.properties.RedisProperties;
import me.chanjar.weixin.qidian.solon.properties.WxQidianProperties;

import com.google.common.collect.Sets;

import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.qidian.config.WxQidianConfigStorage;
import me.chanjar.weixin.qidian.config.impl.WxQidianRedisConfigImpl;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.util.Pool;

/**
 * @author yl TaoYu
 */
@Deprecated
@Configuration
@Condition(onProperty = "${wx.qidian.config-storage.type}=jedis", onClass = JedisPool.class)
public class WxQidianInJedisConfigStorageConfiguration extends AbstractWxQidianConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxQidianConfigStorage.class)
	public WxQidianConfigStorage WxMpConfigStorage(WxQidianProperties properties) {
		WxQidianRedisConfigImpl config = jedisConfigStorage(properties);
		return this.config(config, properties);
	}

	@Deprecated
	  private WxQidianRedisConfigImpl jedisConfigStorage(final WxQidianProperties wxQidianProperties) {
	    Pool<Jedis> jedisPool;
	    String redisHost = wxQidianProperties.getConfigStorage().getRedis().getHost();
	    if (StringUtils.isNotEmpty(redisHost)) {
	      jedisPool = getJedisPool(wxQidianProperties);
	    } else {
	      jedisPool = Solon.context().getBean(JedisPool.class);
	    }
	    WxRedisOps redisOps = new JedisWxRedisOps(jedisPool);
	    WxQidianRedisConfigImpl wxQidianRedisConfig = new WxQidianRedisConfigImpl(redisOps,
	        wxQidianProperties.getConfigStorage().getKeyPrefix());
	    return wxQidianRedisConfig;
	  }

	 @Deprecated
	  private Pool<Jedis> getJedisPool(WxQidianProperties wxQidianProperties) {
	    WxQidianProperties.ConfigStorage storage = wxQidianProperties.getConfigStorage();
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
	    if (StringUtils.isNotEmpty(redis.getSentinelIps())) {
	      Set<String> sentinels = Sets.newHashSet(redis.getSentinelIps().split(","));
	      return new JedisSentinelPool(redis.getSentinelName(), sentinels);
	    }

	    return new JedisPool(config, redis.getHost(), redis.getPort(), redis.getTimeout(), redis.getPassword(),
	        redis.getDatabase());
	  }
}
