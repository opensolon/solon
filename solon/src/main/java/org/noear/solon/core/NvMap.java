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
package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.IgnoreCaseMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 可排序，不区分大小写（Name value map）
 *
 * 用于：参数解析，Header，Param 处理
 *
 * @see Context#paramMap()
 * @author noear
 * @since 1.0
 * */
public class NvMap extends IgnoreCaseMap<String> {
    public static NvMap from(Map map) {
        NvMap d = new NvMap();

        if (map != null) {
            map.forEach((k, v) -> {
                if (k != null && v != null) {
                    d.put(k.toString(), v.toString());
                }
            });
        }

        return d;
    }

    public static NvMap from(String[] args) {
        return from(Arrays.asList(args));
    }

    public static NvMap from(List<String> args) {
        NvMap d = new NvMap();

        if (args != null) {
            for (String arg : args) {
                int index = arg.indexOf('=');
                if (index > 0) {
                    String name = arg.substring(0, index);
                    String value = arg.substring(index + 1);
                    d.put(name.replaceAll("^-*", ""), value);
                } else {
                    d.put(arg.replaceAll("^-*", ""), "");
                }
            }
        }

        return d;
    }

    public NvMap set(String key, String val) {
        put(key, val);
        return this;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getInt(String key, int def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key) {
        return getLong(key, 0L);
    }

    public long getLong(String key, long def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Long.parseLong(temp);
        }
    }

    public double getDouble(String key) {
        return getDouble(key, 0d);
    }

    public double getDouble(String key, double def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Double.parseDouble(temp);
        }
    }

    public boolean getBool(String key, boolean def) {
        if (containsKey(key)) {
            return Boolean.parseBoolean(get(key));
        } else {
            return def;
        }
    }

    /**
     * 转为换一个类实例
     *
     * @deprecated 2.9 {@link #toBean(Class<?>)}
     * */
    @Deprecated
    public <T> T getBean(Class<T> clz) {
        return toBean(clz);
    }

    /**
     * 转为换一个类实例
     *
     * @since 2.9
     * */
    public <T> T toBean(Class<T> clz) {
        return PropsConverter.global().convert(new Props().addAll(this), clz);
    }
}