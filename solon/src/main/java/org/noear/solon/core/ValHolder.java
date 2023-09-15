package org.noear.solon.core;

/**
 * 值容器
 *
 * @author noear
 * @since 1.0
 * @since 2.5
 * */
@Deprecated
public class ValHolder<T> {
    public T value;

    public ValHolder(){
    }

    public ValHolder(T val){
        this.value = val;
    }
}
