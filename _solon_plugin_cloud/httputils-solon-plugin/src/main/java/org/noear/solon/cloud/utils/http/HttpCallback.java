package org.noear.solon.cloud.utils.http;

/**
 * @author noear
 * @since 1.5
 * */
public interface HttpCallback<T1,T2,T3> {
    void callback(T1 t1, T2 t2, T3 t3) throws Exception;
}
