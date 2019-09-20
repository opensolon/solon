package org.noear.solon.core.utils;

/** 线程数据（用于复用） */
public class TlData<T> extends ThreadLocal<T> {
    private T _def;
    public TlData(T def){
        _def = def;
    }

    @Override
    protected T initialValue() {
        return _def;
    }
}
