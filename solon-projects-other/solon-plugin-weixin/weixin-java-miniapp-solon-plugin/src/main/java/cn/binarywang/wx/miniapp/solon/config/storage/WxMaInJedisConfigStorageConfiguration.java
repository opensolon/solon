package cn.binarywang.wx.miniapp.solon.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import cn.binarywang.wx.miniapp.solon.properties.RedisProperties;
import cn.binarywang.wx.miniapp.solon.properties.WxMaProperties;

import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedisBetterConfigImpl;
import me.chanjar.weixin.common.redis.JedisWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author yl TaoYu
 */
@Deprecated
@Configuration
@Condition(onProperty = "${wx.miniapp.config-storage.type}=jedis", onClass = JedisPool.class)
public class WxMaInJedisConfigStorageConfiguration extends AbstractWxMaConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxMaConfig.class)
	public WxMaConfig wxMaConfig(WxMaProperties properties) {
		WxMaRedisBetterConfigImpl config = getWxMaRedisBetterConfigImpl(properties);
		return this.config(config, properties);
	}

	private WxMaRedisBetterConfigImpl getWxMaRedisBetterConfigImpl(final WxMaProperties properties) {
		RedisProperties redisProperties = properties.getConfigStorage().getRedis();
		JedisPool jedisPool;
		if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
			jedisPool = getJedisPool(properties);
		} else {
			jedisPool = Solon.context().getBean(JedisPool.class);
		}
		WxRedisOps redisOps = new JedisWxRedisOps(jedisPool);
		return new WxMaRedisBetterConfigImpl(redisOps, properties.getConfigStorage().getKeyPrefix());
	}

	private JedisPool getJedisPool(final WxMaProperties properties) {
		WxMaProperties.ConfigStorage storage = properties.getConfigStorage();
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

		return new JedisPool(config, redis.getHost(), redis.getPort(), redis.getTimeout(), redis.getPassword(),
				redis.getDatabase());
	}
}
