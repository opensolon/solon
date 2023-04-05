package me.chanjar.weixin.cp.solon.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.cp.solon.properties.RedisProperties;
import me.chanjar.weixin.cp.solon.properties.WxCpProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpRedissonConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.cp.config-storage.type}=redisson", onClass = Redisson.class)
public class WxCpInRedissonConfigStorageConfiguration extends AbstractWxCpConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxCpConfigStorage.class)
	public WxCpConfigStorage WxCpConfigStorage(WxCpProperties properties) {
		WxCpRedissonConfigImpl config = getWxMaInRedissonConfigStorage(properties);
		return this.config(config, properties);
	}

	private WxCpRedissonConfigImpl getWxMaInRedissonConfigStorage(final WxCpProperties properties) {
		RedisProperties redisProperties = properties.getConfigStorage().getRedis();
		RedissonClient redissonClient;
		if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
			redissonClient = getRedissonClient(properties);
		} else {
			redissonClient = Solon.context().getBean(RedissonClient.class);
		}
		return new WxCpRedissonConfigImpl(redissonClient, properties.getConfigStorage().getKeyPrefix());
	}

	private RedissonClient getRedissonClient(final WxCpProperties properties) {
		WxCpProperties.ConfigStorage storage = properties.getConfigStorage();
		RedisProperties redis = storage.getRedis();

		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redis.getHost() + ":" + redis.getPort())
				.setDatabase(redis.getDatabase()).setPassword(redis.getPassword());
		config.setTransportMode(TransportMode.NIO);
		return Redisson.create(config);
	}
}
