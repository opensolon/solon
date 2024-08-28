/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;
import org.noear.solon.lang.Nullable;

import java.util.*;

/**
 * 可排序，不区分大小写，多值（Name values map）
 *
 * 用于：参数解析，Header，Param 处理
 *
 * @see Context#paramMap()
 * @author noear
 * @since 1.0
 * */
public class NvMap extends MultiMap<String> {
    public static NvMap from(Map args) {
        NvMap d = new NvMap();

        if (args != null) {
            args.forEach((k, v) -> {
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
                    d.add(name.replaceAll("^-*", ""), value);
                } else {
                    d.add(arg.replaceAll("^-*", ""), "");
                }
            }
        }

        return d;
    }

    public NvMap set(String key, String val) {
        put(key, val);
        return this;
    }


    /**
     * 获取值并转为 int
     *
     * @param key 键
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * 获取值并转为 long
     *
     * @param key 键
     * @param def 默认值
     */
    public int getInt(String key, int def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Integer.parseInt(temp);
        }
    }

    /**
     * 获取值并转为 long
     *
     * @param key 键
     */
    public long getLong(String key) {
        return getLong(key, 0l);
    }

    /**
     * 获取值并转为 long
     *
     * @param key 键
     * @param def 默认值
     */
    public long getLong(String key, long def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Long.parseLong(temp);
        }
    }

    /**
     * 获取值并转为 double
     *
     * @param key 键
     */
    public double getDouble(String key) {
        return getDouble(key, 0d);
    }

    /**
     * 获取值并转为 double
     *
     * @param key 键
     * @param def 默认值
     */
    public double getDouble(String key, double def) {
        String temp = get(key);
        if (Utils.isEmpty(temp)) {
            return def;
        } else {
            return Double.parseDouble(temp);
        }
    }

    /**
     * 获取值并转为 bool
     *
     * @param key 键
     */
    public boolean getBool(String key) {
        return getBool(key, false);
    }

    /**
     * 获取值并转为 bool
     *
     * @param key 键
     * @param def 默认值
     */
    public boolean getBool(String key, boolean def) {
        if (containsKey(key)) {
            return Boolean.parseBoolean(get(key));
        } else {
            return def;
        }
    }

    /**
     * 获取值并转为 bean
     */
    public <T> T getBean(Class<T> clz) {
        return PropsConverter.global().convert(new Props(this), clz);
    }

    /**
     * 获取多值
     *
     * @param key 键
     */
    public @Nullable String[] getArray(String key) {
        KeyValues<String> tmp = innerMap.get(key);
        if (tmp == null) {
            return null;
        } else {
            return tmp.getValues().toArray(new String[tmp.getValues().size()]);
        }
    }
}