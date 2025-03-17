package org.noear.solon.security.web;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.core.handle.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全过滤器
 *
 * @author noear
 * @since 3.1
 */
public class SecurityFilter implements Filter {
    private Handler[] handlers;

    public SecurityFilter(Handler... handlers) {
        this.handlers = handlers;
    }

    /**
     * 执行过滤
     */
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        for (Handler handler : handlers) {
            handler.handle(ctx);
        }

        chain.doFilter(ctx);
    }
}