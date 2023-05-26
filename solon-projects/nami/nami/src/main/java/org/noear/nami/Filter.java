package org.noear.nami;

/**
 * Nami - 拦截器
 *
 * @author noear
 * @since 1.4
 */
public interface Filter {
    /**
     * 拦截
     *
     * @param inv 调用者
     * */
    Result doFilter(Invocation inv) throws Throwable;
}
