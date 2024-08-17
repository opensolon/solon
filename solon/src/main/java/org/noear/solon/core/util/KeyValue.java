package org.noear.solon.core.util;

/**
 * 键值
 * */
public class KeyValue<T> {
    private String key;
    private T value;

    public KeyValue(String key, T value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 获取键
     */
    public String getKey() {
        return key;
    }

    /**
     * 获取值
     */
    public T getValue() {
        return value;
    }
}