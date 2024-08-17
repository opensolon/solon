package org.noear.solon.core.util;

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