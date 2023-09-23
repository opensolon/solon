package org.noear.solon.core;

/**
 * 值容器
 *
 * @author noear
 * @since 1.0
 * @deprecated  2.5
 * */
@SuppressWarnings("removal")
@Deprecated
public class ValHolder<T> {
    public T value;

    public ValHolder(){
    }

    public ValHolder(T val){
        this.value = val;
    }
}
