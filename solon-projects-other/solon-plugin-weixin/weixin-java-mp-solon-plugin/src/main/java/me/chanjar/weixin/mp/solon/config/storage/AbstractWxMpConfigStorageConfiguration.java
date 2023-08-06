package me.chanjar.weixin.mp.solon.config.storage;

import me.chanjar.weixin.mp.solon.properties.WxMpProperties;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * @author yl TaoYu
 */
public abstract class AbstractWxMpConfigStorageConfiguration {

	protected WxMpDefaultConfigImpl config(WxMpDefaultConfigImpl config, WxMpProperties properties) {
		WxMpProperties.ConfigStorage configStorageProperties = properties.getConfigStorage();
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
