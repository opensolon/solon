package org.noear.solon.cloud.model;


import org.noear.solon.Utils;
import org.noear.solon.core.PropsLoader;
import org.noear.solon.core.wrap.ClassWrap;

import java.io.IOException;
import java.util.Map;
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
    private String key;
    /**
     * 值
     */
    private String value;

    /**
     * 版本号
     * */
    private long version;


    public Config(String key, String value, long version) {
        this.key = key;
        this.value = value;
        this.version = version;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    public Config value(String value, long version) {
        this.value = value;
        this.version = version;
        this._props = null;

        return this;
    }
    
    public long version() {
        return version;
    }

    private Properties _props;

    public Properties toProps() {
        if (_props == null) {
            _props = Utils.buildProperties(value);

            for (Map.Entry<Object, Object> kv : _props.entrySet()) {
                if (kv.getValue() instanceof String) {
                    String tmpV = (String) kv.getValue();
                    if (tmpV.startsWith("${") && tmpV.endsWith("}")) {
                        String tmpK = tmpV.substring(2, tmpV.length() - 1);
                        tmpV = _props.getProperty(tmpK);
                        _props.put(kv.getKey(), tmpV);
                    }
                }
            }
        }

        return _props;
    }

    public <T> T toBean(Class<T> clz) {
        return ClassWrap.get(clz).newBy(toProps());
    }
}
