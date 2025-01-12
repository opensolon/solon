/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.model;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.PropsConverter;
import org.noear.solon.core.util.PropUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * 配置模型
 *
 * @author noear
 * @since 1.2
 */
public class Config implements Serializable {
    private String group;
    private String key;
    private String value;
    private long version;

    public Config(String group, String key, String value, long version) {
        this.group = group;
        this.key = key;
        this.value = value;
        this.version = version;
    }


    /**
     * 更新的值与版本号
     */
    public Config updateValue(String value, long version) {
        this.value = value;
        this.version = version;
        this._props = null;

        return this;
    }

    /**
     * 获取分组
     */
    public String group() {
        return group;
    }

    /**
     * 获取配置键
     */
    public String key() {
        return key;
    }

    /**
     * 获取值
     */
    public String value() {
        return value;
    }

    /**
     * 获取版本号
     */
    public long version() {
        return version;
    }

    private transient Properties _props;

    /**
     * 转换为属性格式
     */
    public Properties toProps() {
        if (_props == null) {
            _props = Utils.buildProperties(value);

            for (Map.Entry<Object, Object> kv : _props.entrySet()) {
                if (kv.getValue() instanceof String) {
                    String tmpV = PropUtil.getByTml(Solon.cfg(), _props, (String) kv.getValue(), kv.getKey().toString());

                    if (tmpV == null) {
                        tmpV = (String) kv.getValue();
                    }

                    _props.put(kv.getKey(), tmpV);
                }
            }
        }

        return _props;
    }

    /**
     * 转换为Bean
     */
    public <T> T toBean(Class<T> clz) {
        Properties props = toProps();
        return PropsConverter.global().convert(props, null, clz, clz);
    }

    @Override
    public String toString() {
        return "Config{" +
                "key='" + key + '\'' +
                ", group='" + group + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
