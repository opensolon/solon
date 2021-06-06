package org.noear.nami;

/**
 * Nami - 拦截器
 *
 * @author noear
 * @since 1.4
 */
public interface Interceptor {
    /**
     * 拦截
     *
     * @param inv 调用者
     * */
    Result doIntercept(Invocation inv) throws Throwable;
}
