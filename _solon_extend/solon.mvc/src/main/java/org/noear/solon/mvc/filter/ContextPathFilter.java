package org.noear.solon.mvc.filter;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * 提供 ContextPath 类似的功能
 *
 * @author noear
 * @since 1.8
 */
public class ContextPathFilter implements Filter {
    private String path;

    /**
     * @param path '/demo/'
     * */
    public ContextPathFilter(String path) {
        this.path = path;
    }

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        if (ctx.pathNew().startsWith(path)) {
            ctx.pathNew(ctx.path().substring(path.length() - 1));
        }

        chain.doFilter(ctx);
    }
}
