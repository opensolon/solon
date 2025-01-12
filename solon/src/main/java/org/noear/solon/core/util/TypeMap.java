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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 类型字典
 *
 * @author noear
 * @since 2.9
 */
public class TypeMap<T> {
    //分类字典
    private Map<Class<?>, T> map;
    //默认构建器
    private T def;

    /**
     * 配置默认值
     */
    public void def(T item) {
        this.def = item;
    }

    /**
     * 配置类型值
     */
    public void put(Class<?> type, T item) {
        if (map == null) {
            //要使用有顺序的 LinkedHashMap
            map = new LinkedHashMap<>();
        }

        map.putIfAbsent(type, item);
    }


    /**
     * 获取值
     */
    public T get(Class<?> type) {
        if (map != null) {
            for (Map.Entry<Class<?>, T> kv : map.entrySet()) {
                if (kv.getKey().isAssignableFrom(type)) {
                    return kv.getValue();
                }
            }
        }

        return def;
    }
}