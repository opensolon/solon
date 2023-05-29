package me.chanjar.weixin.qidian.solon.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.qidian.solon.properties.RedisProperties;
import me.chanjar.weixin.qidian.solon.properties.WxQidianProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import me.chanjar.weixin.qidian.config.WxQidianConfigStorage;
import me.chanjar.weixin.qidian.config.impl.WxQidianRedissonConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.qidian.config-storage.type}=redisson", onClass = Redisson.class)
public class WxQidianInRedissonConfigStorageConfiguration extends AbstractWxQidianConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxQidianConfigStorage.class)
	public WxQidianConfigStorage WxMpConfigStorage(WxQidianProperties properties) {
		WxQidianRedissonConfigImpl config = redissonConfigStorage(properties);
		return this.config(config, properties);
	}

	private WxQidianRedissonConfigImpl redissonConfigStorage(final WxQidianProperties properties) {
		RedisProperties redisProperties = properties.getConfigStorage().getRedis();
		RedissonClient redissonClient;
		if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
			redissonClient = getRedissonClient(properties);
		} else {
			redissonClient = Solon.context().getBean(RedissonClient.class);
		}
		return new WxQidianRedissonConfigImpl(redissonClient, properties.getConfigStorage().getKeyPrefix());
	}

	private RedissonClient getRedissonClient(final WxQidianProperties properties) {
		WxQidianProperties.ConfigStorage storage = properties.getConfigStorage();
		RedisProperties redis = storage.getRedis();

		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redis.getHost() + ":" + redis.getPort())
				.setDatabase(redis.getDatabase()).setPassword(redis.getPassword());
		config.setTransportMode(TransportMode.NIO);
		return Redisson.create(config);
	}
}
