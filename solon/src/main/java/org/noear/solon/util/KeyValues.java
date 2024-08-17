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
package org.noear.solon.util;

import java.util.Arrays;
import java.util.List;

/**
 * 键值组
 * */
public class KeyValues<T> {
    private String key;
    private List<T> values;

    public KeyValues(String key) {
        this.key = key;
    }

    /**
     * 获取键
     */
    public String getKey() {
        return key;
    }

    /**
     * 添加值
     */
    public void addValue(T value) {
        values.add(value);
    }

    /**
     * 替换值
     */
    public void setValues(List<T> values) {
        this.values = values;
    }

    /**
     * 替换值
     */
    public void setValues(T... values) {
        this.values = Arrays.asList(values);
    }

    /**
     * 获取值组
     */
    public List<T> getValues() {
        return values;
    }

    /**
     * 获取第一个值
     */
    public T getFirstValue() {
        return values.get(0);
    }
}