package org.noear.solon.extend.cloud.model;


import org.noear.solon.Utils;
import org.noear.solon.core.PropsLoader;

import java.io.IOException;
import java.util.Properties;

/**
 * 配置模型
 *
 * @author noear
 * @since 1.2
 */
public class Config {
    /**
     * 配置键
     */
    public String key;
    /**
     * 值
     */
    public String value;


    public Config() {

    }

    public Config(String key, String value) {
        this.key = key;
        this.value = value;
    }


    private Properties _props;

    public Properties toProps() {
        if (_props == null) {
            _props = Utils.buildProperties(value);
        }
        return _props;
    }
}
