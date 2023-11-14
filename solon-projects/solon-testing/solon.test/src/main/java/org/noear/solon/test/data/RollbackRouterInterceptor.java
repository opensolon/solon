package org.noear.solon.test.data;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;

/**
 * @author noear
 * @since 2.6
 */
public class RollbackRouterInterceptor implements RouterInterceptor {
    private static RollbackRouterInterceptor instance = new RollbackRouterInterceptor();

    public static RollbackRouterInterceptor getInstance() {
        return instance;
    }

    private RollbackRouterInterceptor(){

    }

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        TranUtils.execute(new TranAnno(), () -> {
            chain.doIntercept(ctx, mainHandler);
            throw new RollbackException();
        });
    }
}
