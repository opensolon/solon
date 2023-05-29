package me.chanjar.weixin.mp.solon.config.storage;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.mp.solon.properties.RedisProperties;
import me.chanjar.weixin.mp.solon.properties.WxMpProperties;

import com.google.common.collect.Sets;

import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
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
@Condition(onProperty = "${wx.mp.config-storage.type}=jedis", onClass = JedisPool.class)
public class WxMpInJedisConfigStorageConfiguration extends AbstractWxMpConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxMpConfigStorage.class)
	public WxMpConfigStorage WxMpConfigStorage(WxMpProperties properties) {
		WxMpRedisConfigImpl config = jedisConfigStorage(properties);
		return this.config(config, properties);
	}

	@Deprecated
	private WxMpRedisConfigImpl jedisConfigStorage(final WxMpProperties wxMpProperties) {
		Pool<Jedis> jedisPool;
		if (wxMpProperties.getConfigStorage() != null && wxMpProperties.getConfigStorage().getRedis() != null
				&& StringUtils.isNotEmpty(wxMpProperties.getConfigStorage().getRedis().getHost())) {
			jedisPool = getJedisPool(wxMpProperties);
		} else {
			jedisPool = Solon.context().getBean(JedisPool.class);
		}
		WxRedisOps redisOps = new JedisWxRedisOps(jedisPool);
		WxMpRedisConfigImpl wxMpRedisConfig = new WxMpRedisConfigImpl(redisOps,
				wxMpProperties.getConfigStorage().getKeyPrefix());
		return wxMpRedisConfig;
	}

	@Deprecated
	private Pool<Jedis> getJedisPool(final WxMpProperties wxMpProperties) {
		RedisProperties redis = wxMpProperties.getConfigStorage().getRedis();

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
