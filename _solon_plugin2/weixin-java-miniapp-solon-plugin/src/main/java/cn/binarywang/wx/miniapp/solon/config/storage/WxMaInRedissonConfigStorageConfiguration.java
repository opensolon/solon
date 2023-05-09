package cn.binarywang.wx.miniapp.solon.config.storage;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import cn.binarywang.wx.miniapp.solon.properties.RedisProperties;
import cn.binarywang.wx.miniapp.solon.properties.WxMaProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedissonConfigImpl;

/**
 * @author yl TaoYu
 */
@Configuration
@Condition(onProperty = "${wx.miniapp.config-storage.type}=redisson", onClass = Redisson.class)
public class WxMaInRedissonConfigStorageConfiguration extends AbstractWxMaConfigStorageConfiguration {

	@Bean
	@Condition(onMissingBean = WxMaConfig.class)
	public WxMaConfig wxMaConfig(WxMaProperties properties) {
		WxMaRedissonConfigImpl config = getWxMaInRedissonConfigStorage(properties);
		return this.config(config, properties);
	}

	private WxMaRedissonConfigImpl getWxMaInRedissonConfigStorage(final WxMaProperties properties) {
		RedisProperties redisProperties = properties.getConfigStorage().getRedis();
		RedissonClient redissonClient;
		if (redisProperties != null && StringUtils.isNotEmpty(redisProperties.getHost())) {
			redissonClient = getRedissonClient(properties);
		} else {
			redissonClient = Solon.context().getBean(RedissonClient.class);
		}
		return new WxMaRedissonConfigImpl(redissonClient, properties.getConfigStorage().getKeyPrefix());
	}

	private RedissonClient getRedissonClient(final WxMaProperties properties) {
		WxMaProperties.ConfigStorage storage = properties.getConfigStorage();
		RedisProperties redis = storage.getRedis();

		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redis.getHost() + ":" + redis.getPort())
				.setDatabase(redis.getDatabase()).setPassword(redis.getPassword());
		config.setTransportMode(TransportMode.NIO);
		return Redisson.create(config);
	}
}
