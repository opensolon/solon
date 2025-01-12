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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 键值组
 * */
public class KeyValues<T> implements Serializable {
    private String key;
    private List<T> values;

    public KeyValues(String key) {
        this.key = key;
    }

    private void initValues(boolean reset) {
        if (values == null) {
            values = new ArrayList<>();
        } else {
            if (reset) {
                values.clear();
            }
        }
    }

    /**
     * 获取键
     */
    public String getKey() {
        return key;
    }

    /**
     * 移除值
     */
    public void removeValue(T value) {
        if (values != null) {
            values.remove(value);
        }
    }

    /**
     * 添加值
     */
    public void addValue(T value) {
        initValues(false);
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
        initValues(true);

        for (int i = 0; i < values.length; i++) {
            this.values.add(values[i]);
        }
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
        if (values == null) {
            return null;
        }

        return values.get(0);
    }

    /**
     * 获取最后值
     */
    public T getLastValue() {
        if (values == null) {
            return null;
        }

        if (values.size() > 0) {
            return values.get(values.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "key='" + key + '\'' +
                ", values=" + values +
                '}';
    }
}