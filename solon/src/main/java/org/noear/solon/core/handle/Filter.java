package org.noear.solon.core.handle;

/**
 * 过滤器
 *
 * @author noear
 * @since 1.3
 * */
public interface Filter {
    /**
     * 过滤
     *
     * @param ctx 上下文
     * */
    void doFilter(Context ctx, FilterChain chain) throws Throwable;
}
