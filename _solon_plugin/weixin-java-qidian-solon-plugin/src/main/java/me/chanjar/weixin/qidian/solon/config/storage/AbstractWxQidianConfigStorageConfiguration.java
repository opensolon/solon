package me.chanjar.weixin.qidian.solon.config.storage;

import me.chanjar.weixin.qidian.solon.properties.WxQidianProperties;

import me.chanjar.weixin.qidian.config.impl.WxQidianDefaultConfigImpl;

/**
 * @author yl TaoYu
 */
public abstract class AbstractWxQidianConfigStorageConfiguration {

	protected WxQidianDefaultConfigImpl config(WxQidianDefaultConfigImpl config, WxQidianProperties properties) {
		WxQidianProperties.ConfigStorage configStorageProperties = properties.getConfigStorage();
		config.setAppId(properties.getAppId());
		config.setSecret(properties.getSecret());
		config.setToken(properties.getToken());
		config.setAesKey(properties.getAesKey());

		config.setHttpProxyHost(configStorageProperties.getHttpProxyHost());
		config.setHttpProxyUsername(configStorageProperties.getHttpProxyUsername());
		config.setHttpProxyPassword(configStorageProperties.getHttpProxyPassword());
		if (configStorageProperties.getHttpProxyPort() != null) {
			config.setHttpProxyPort(configStorageProperties.getHttpProxyPort());
		}
		return config;
	}
}
