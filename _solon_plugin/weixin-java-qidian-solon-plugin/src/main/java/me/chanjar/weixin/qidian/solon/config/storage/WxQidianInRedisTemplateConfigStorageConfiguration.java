package me.chanjar.weixin.qidian.solon.config.storage;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.qidian.solon.properties.WxQidianProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.qidian.config.WxQidianConfigStorage;
import me.chanjar.weixin.qidian.config.impl.WxQidianRedisConfigImpl;

/**
 * @author yl TaoYu
 */
@Deprecated
@Slf4j
@Configuration
@Condition(onProperty = "${wx.qidian.config-storage.type}=redistemplate", onClass = StringRedisTemplate.class)
public class WxQidianInRedisTemplateConfigStorageConfiguration extends AbstractWxQidianConfigStorageConfiguration {

	@Deprecated
	@Bean
	@Condition(onMissingBean = WxQidianConfigStorage.class)
	public WxQidianConfigStorage WxMpConfigStorage(WxQidianProperties properties) {
		WxQidianRedisConfigImpl config = redisTemplateConfigStorage(properties);
		return this.config(config, properties);
	}

	@Deprecated
	private WxQidianRedisConfigImpl redisTemplateConfigStorage(final WxQidianProperties wxQidianProperties) {
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
		WxQidianRedisConfigImpl wxMpRedisConfig = new WxQidianRedisConfigImpl(redisOps,
				wxQidianProperties.getConfigStorage().getKeyPrefix());

		return wxMpRedisConfig;
	}

}
