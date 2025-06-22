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

/**
 * 多值，忽略大小写的LinkedMap
 *
 * @author noear
 * @since 2.9
 */
public class MultiMap<T> implements Iterable<KeyValues<T>>, Serializable {
    protected final Map<String, KeyValues<T>> innerMap;
    protected final boolean ignoreCase;

    public MultiMap() {
        this(true);
    }

    public MultiMap(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.innerMap = createMap(0);
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
}