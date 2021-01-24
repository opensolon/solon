package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudConfigHandler;
import org.noear.solon.cloud.model.Config;

/**
 * 云端配置服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudConfigService {
    /**
     * 拉取配置
     */
    Config pull(String group, String key);

    /**
     * 推送配置
     */
    boolean push(String group, String key, String value);

    /**
     * 移除配置
     */
    boolean remove(String group, String key);

    /**
     * 关注配置
     */
    void attention(String group, String key, CloudConfigHandler observer);
}
