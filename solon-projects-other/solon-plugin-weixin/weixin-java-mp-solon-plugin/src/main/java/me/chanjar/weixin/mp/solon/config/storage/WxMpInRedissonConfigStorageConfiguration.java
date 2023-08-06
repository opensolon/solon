package me.chanjar.weixin.mp.solon.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.mp.solon.properties.RedisProperties;
import me.chanjar.weixin.mp.solon.properties.WxMpProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpRedissonConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.mp.config-storage.type}=redisson", onClass = Redisson.class)
public class WxMpInRedissonConfigStorageConfiguration extends AbstractWxMpConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxMpConfigStorage.class)
	public WxMpConfigStorage WxMpConfigStorage(WxMpProperties properties) {
		WxMpRedissonConfigImpl config = redissonConfigStorage(properties);
		return this.config(config, properties);
	}

	private WxMpRedissonConfigImpl redissonConfigStorage(final WxMpProperties properties) {
		RedisProperties redisProperties = properties.getConfigStorage().getRedis();
		RedissonClient redissonClient;
		if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
			redissonClient = getRedissonClient(properties);
		} else {
			redissonClient = Solon.context().getBean(RedissonClient.class);
		}
		return new WxMpRedissonConfigImpl(redissonClient, properties.getConfigStorage().getKeyPrefix());
	}

	private RedissonClient getRedissonClient(final WxMpProperties properties) {
		WxMpProperties.ConfigStorage storage = properties.getConfigStorage();
		RedisProperties redis = storage.getRedis();

		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redis.getHost() + ":" + redis.getPort())
				.setDatabase(redis.getDatabase()).setPassword(redis.getPassword());
		config.setTransportMode(TransportMode.NIO);
		return Redisson.create(config);
	}
}
