package org.noear.solon.core.route;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.lang.Nullable;

import java.util.List;

/**
 * 路由拦截器调用链实现
 *
 * @author noear
 * @since 1.3
 * */
public class RouterInterceptorChainImpl implements RouterInterceptorChain {
    private final List<RouterInterceptorEntity> interceptorList;
    private int index;

    public RouterInterceptorChainImpl(List<RouterInterceptorEntity> interceptorList) {
        this.interceptorList = interceptorList;
        this.index = 0;
    }

    @Override
    public void doIntercept(Context ctx, @Nullable Handler mainHandler) throws Throwable {
        interceptorList.get(index++).interceptor.doIntercept(ctx, mainHandler, this);
    }
}
