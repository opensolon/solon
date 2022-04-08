package org.noear.solon.core;

/**
 * 值容器
 *
 * @author noear
 * @since 1.0
 * */
public class ValHolder<T> {
    private T value;

    /**
     * @since 1.6
     * */
    public T getValue() {
        return value;
    }

    /**
     * @since 1.6
     * */
    public void setValue(T value) {
        this.value = value;
    }

    public ValHolder(){
    }

    public ValHolder(T val){
        this.value = val;
    }
}
