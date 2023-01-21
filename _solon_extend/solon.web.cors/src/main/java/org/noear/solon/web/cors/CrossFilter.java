package org.noear.solon.web.cors;

import org.noear.solon.core.handle.*;

/**
 * 跨域处理
 *
 * @author noear
 * @since 1.9
 */
public class CrossFilter extends AbstractCross<CrossFilter> implements Filter {

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        doHandle(ctx);

        if (ctx.getHandled() == false) {
            chain.doFilter(ctx);
        }
    }
}
