package org.noear.solon.core.handle;

/**
 * 过滤器调用链
 *
 * @author noear
 * @since 1.3
 * */
public interface FilterChain {
    /**
     * 过滤
     *
     * @param ctx 上下文
     * */
    void doFilter(Context ctx);
}
