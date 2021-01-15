package org.noear.solon.extend.cloud.service;

import org.noear.solon.extend.cloud.model.Config;
import org.noear.solon.extend.cloud.model.ConfigSet;

/**
 * @author noear
 * @since 1.2
 */
public interface CloudConfigService {
    /**
     * 获取配置
     */
    Config get(String group, String key);

    /**
     * 获取配置
     * */
    ConfigSet get(String group);

    /**
     * 设置配置
     */
    boolean set(String group, String key, String value);

    /**
     * 移除配置
     */
    boolean remove(String group, String key);
}
