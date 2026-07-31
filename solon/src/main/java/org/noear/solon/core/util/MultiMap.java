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
package org.noear.solon.core.util;

import org.noear.solon.lang.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * 多值，忽略大小写的LinkedMap
 *
 * @author noear
 * @since 2.9
 */
public class MultiMap<T> implements Iterable<KeyValues<T>>, Serializable {
    protected final Map<String, KeyValues<T>> innerMap;
    protected final boolean ignoreCase;
    private transient List<String> flags;

    public MultiMap() {
        this(true);
    }

    public MultiMap(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.innerMap = createMap(0);
    }

    //解析 args 用
    public List<String> flags() {
        if (flags == null) {
            flags = new ArrayList<>();
        }

        return flags;
    }

    public String flagAt(int index) {
        if (flags == null) {
            return null;
        } else {
            if (flags.size() > index) {
                return flags.get(index);
            } else {
                return null;
            }
        }
    }

    @Override
    public Iterator<KeyValues<T>> iterator() {
        return innerMap.values().iterator();
    }

    /**
     * 持有
     */
    public KeyValues<T> holder(String key) {
        return innerMap.computeIfAbsent(key, k -> new KeyValues<>(key));
    }

    /**
     * 键集合
     */
    public Set<String> keySet() {
        return innerMap.keySet();
    }

    /**
     * 是否包含键
     */
    public boolean containsKey(String key) {
        return innerMap.containsKey(key);
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    /**
     * 大小
     */
    public int size() {
        return innerMap.size();
    }


    /**
     * 设置值
     *
     * @param key 键
     * @param val 值
     */
    public void put(String key, T val) {
        holder(key).setValues(val);
    }

    /**
     * 设置值如果没有
     *
     * @param key 键
     * @param val 值
     */
    public void putIfAbsent(String key, T val) {
        if (innerMap.containsKey(key) == false) {
            holder(key).setValues(val);
        }
    }

    /**
     * 设置所有值
     */
    public void putAll(Map<String, T> values) {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            holder(entry.getKey()).setValues(entry.getValue());
        }
    }

    /**
     * 添加值
     *
     * @param key 键
     * @param val 值
     */
    public void add(String key, T val) {
        holder(key).addValue(val);
    }

    /**
     * 设置所有值
     */
    public void addAll(Map<String, T> values) {
        for (Map.Entry<String, T> entry : values.entrySet()) {
            holder(entry.getKey()).addValue(entry.getValue());
        }
    }

    /**
     * 移除键
     */
    public void remove(String key) {
        innerMap.remove(key);
    }

    /**
     * 清理
     */
    public void clear() {
        innerMap.clear();
    }

    /**
     * 获取多值
     *
     * @param key 键
     */
    public @Nullable List<T> getAll(String key) {
        KeyValues<T> tmp = innerMap.get(key);
        if (tmp == null) {
            return null;
        } else {
            return tmp.getValues();
        }
    }

    /**
     * 获取值
     *
     * @param key 键
     */
    public @Nullable T get(String key) {
        KeyValues<T> tmp = innerMap.get(key);
        if (tmp == null) {
            return null;
        } else {
            return tmp.getFirstValue();
        }
    }

    /**
     * 获取值或默认
     *
     * @param key 键
     * @param def 默认值
     */
    public @Nullable T getOrDefault(String key, T def) {
        T tmp = get(key);
        return tmp == null ? def : tmp;
    }

    /**
     * 转为单值 Map
     */
    public Map<String, T> toValueMap() {
        Map<String, T> tmp = createMap(size());
        for (KeyValues<T> kv : innerMap.values()) {
            tmp.put(kv.getKey(), kv.getFirstValue());
        }

        return tmp;
    }

    /**
     * 转为多值 Map
     */
    public Map<String, List<T>> toValuesMap() {
        Map<String, List<T>> tmp = createMap(size());
        for (KeyValues<T> kv : innerMap.values()) {
            tmp.put(kv.getKey(), kv.getValues());
        }

        return tmp;
    }

    protected <V> Map<String, V> createMap(int size) {
        if (ignoreCase) {
            if (size > 0) {
                return new IgnoreCaseMap<>(size);
            } else {
                return new IgnoreCaseMap<>();
            }
        } else {
            if (size > 0) {
                return new LinkedHashMap<>(size);
            } else {
                return new LinkedHashMap<>();
            }
        }
    }

    @Override
    public String toString() {
        return innerMap.values().toString();
    }

    /// ////////

    public static MultiMap<String> from(String[] args) {
        return from(args, null);
    }

    /**
     * allowKeys 为null 时：贪婪模式（from(args) 路径），对所有选项做lookahead
     * allowKeys 非null 时：精确模式，仅对声明了需要值的 key 做 lookahead
     *
     * @since 4.1
     * */
    public static MultiMap<String> from(String[] args, Set<String> allowKeys) {
        // 与 IgnoreCaseMap 行为一致，allowKeys 使用大小写不敏感的 Set
        if (Assert.isNotEmpty(allowKeys)) {
            Set<String> normalized = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            normalized.addAll(allowKeys);
            allowKeys = normalized;
        }

        MultiMap<String> d = new MultiMap<>();
        List<String> tmp = new ArrayList<>();

        // 第一轮：处理 = 格式
        parseEqualsFormat(args, d, tmp);

        // 第二轮：处理剩余参数
        boolean endOfOptions = false;
        for (int i = 0, len = tmp.size(); i < len; i++) {
            String arg = tmp.get(i);

            if (endOfOptions) {
                d.flags().add(arg);
                continue;
            }

            if ("--".equals(arg)) {
                endOfOptions = true;
                continue;
            }

            if ("-".equals(arg)) {
                d.flags().add(arg);
                continue;
            }

            if (arg.startsWith("-") == false) {
                // positional：只进入 flags，不入 map
                d.flags().add(arg);
                continue;
            }

            // 选项（以 - 或 -- 开头）
            String name;
            if (arg.startsWith("--")) {
                name = arg.substring(2);
            } else {
                name = arg.substring(1);
            }

            // allowKeys 为null 时：贪婪模式（from(args) 路径），对所有选项做lookahead
            // allowKeys 非null 时：精确模式，仅对声明了需要值的 key 做 lookahead
            if (allowKeys == null || allowKeys.contains(name)) {
                if (i < len - 1) {
                    String arg2 = tmp.get(i + 1);
                    if (arg2.startsWith("-") == false) {
                        d.add(name, arg2);
                        i += 1;
                        continue;
                    }
                }
            }

            // 无值，作为布尔 flag
            d.putIfAbsent(name, "");
            d.flags().add(name);
        }

        return d;
    }

    /**
     * 第一轮：处理 key=value 格式，剩余参数进入 tmp
     */
    private static void parseEqualsFormat(String[] args, MultiMap<String> d, List<String> tmp) {
        if (args == null) {
            return;
        }
        for (String arg : args) {
            int index = arg.indexOf('=');
            if (index > 0) {
                String rawName = arg.substring(0, index);
                String name;
                if (rawName.startsWith("--")) {
                    name = rawName.substring(2);
                } else if (rawName.startsWith("-") && rawName.length() > 1) {
                    name = rawName.substring(1);
                } else {
                    // 无前缀或 - 单独，按普通参数处理
                    tmp.add(arg);
                    continue;
                }
                // 防止 --=value 产生空键（如 rawName="--"，substring(2)=""）
                if (name.isEmpty()) {
                    tmp.add(arg);
                    continue;
                }
                d.add(name, arg.substring(index + 1));
            } else {
                tmp.add(arg);
            }
        }
    }

    /**
     * @since 3.6
     * */
    public MultiMap then(Consumer<MultiMap<T>> consumer) {
        consumer.accept(this);
        return this;
    }
}