package org.noear.solon.cloud.model;

import org.noear.solon.core.Props;

import java.util.Map;

/**
 * 配置集合模型
 *
 * @author noear
 * @since 1.2
 */
public class ConfigSet {
    /**
     * 集合名
     */
    public String group;
    /**
     * 配置
     */
    protected Map<String, Config> configMap;

    public Config get(String key) {
        return configMap.get(key);
    }

    public Props toProps() {
        Props props = new Props();
        return props;
    }
}
