package org.noear.solon.web.cors;

import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

/**
 * 跨域处理
 *
 * @author noear
 * @since 1.12
 */
public class CrossInterceptor extends AbstractCross<CrossInterceptor> implements RouterInterceptor {

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        doHandle(ctx);

        if (ctx.getHandled() == false) {
            chain.doIntercept(ctx, mainHandler);
        }
    }
}
