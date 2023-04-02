package me.chanjar.weixin.mp.solon.config.storage;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.mp.solon.properties.WxMpProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;

/**
 * @author yl TaoYu
 */
@Deprecated
@Slf4j
@Configuration
@Condition(onProperty = "${wx.mp.config-storage.type}=redistemplate", onClass = StringRedisTemplate.class)
public class WxMpInRedisTemplateConfigStorageConfiguration extends AbstractWxMpConfigStorageConfiguration {

	@Deprecated
	@Bean
	@Condition(onMissingBean = WxMpConfigStorage.class)
	public WxMpConfigStorage WxMpConfigStorage(WxMpProperties properties) {
		WxMpRedisConfigImpl config = redisTemplateConfigStorage(properties);
		return this.config(config, properties);
	}

	@Deprecated
	private WxMpRedisConfigImpl redisTemplateConfigStorage(final WxMpProperties wxMpProperties) {
		StringRedisTemplate redisTemplate = null;
		try {
			redisTemplate = Solon.context().getBean(StringRedisTemplate.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		try {
			if (null == redisTemplate) {
				redisTemplate = (StringRedisTemplate) Solon.context().getBean("stringRedisTemplate");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (null == redisTemplate) {
			redisTemplate = (StringRedisTemplate) Solon.context().getBean("redisTemplate");
		}

		WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
		WxMpRedisConfigImpl wxMpRedisConfig = new WxMpRedisConfigImpl(redisOps,
				wxMpProperties.getConfigStorage().getKeyPrefix());

		return wxMpRedisConfig;
	}
}
