package org.noear.solon.cloud.utils;

public interface ConsumerEx3<T1,T2,T3> {
    void accept(T1 t1, T2 t2, T3 t3) throws Exception;
}
