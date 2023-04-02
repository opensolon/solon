package me.chanjar.weixin.cp.solon.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.cp.solon.properties.WxCpProperties;

import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;

/**
 * 企业微信平台相关服务自动注册
 *
 * @author yl created on 2021/12/6
 */
@Configuration
@RequiredArgsConstructor
public class WxCpServiceAutoConfiguration {

	@Bean
	@Condition(onMissingBean = WxCpService.class, onClass = WxCpConfigStorage.class)
	public WxCpService wxCpService(WxCpConfigStorage wxCpConfigStorage,WxCpProperties wxCpProperties) {
		WxCpService wxCpService = new WxCpServiceImpl();
		wxCpService.setWxCpConfigStorage(wxCpConfigStorage);

		WxCpProperties.ConfigStorage storage = wxCpProperties.getConfigStorage();
		int maxRetryTimes = storage.getMaxRetryTimes();
		if (maxRetryTimes < 0) {
			maxRetryTimes = 0;
		}
		int retrySleepMillis = storage.getRetrySleepMillis();
		if (retrySleepMillis < 0) {
			retrySleepMillis = 1000;
		}
		wxCpService.setRetrySleepMillis(retrySleepMillis);
		wxCpService.setMaxRetryTimes(maxRetryTimes);
		return wxCpService;
	}
}
