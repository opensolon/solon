package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheRemoveInterceptor implements XInterceptor {
    @Override
    public Object doIntercept(Object obj, Object[] args, XInterceptorChain chain) throws Throwable {
        Object tmp = chain.doIntercept(obj, args);

        XCacheRemove anno = chain.method().getAnnotation(XCacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, chain.method().getMethod(), chain.method().getParameters(), args);

        return tmp;
    }
}
