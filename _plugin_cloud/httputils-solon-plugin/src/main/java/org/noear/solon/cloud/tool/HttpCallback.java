package org.noear.solon.cloud.tool;

public interface HttpCallback<T1,T2,T3> {
    void callback(T1 t1, T2 t2, T3 t3) throws Exception;
}
