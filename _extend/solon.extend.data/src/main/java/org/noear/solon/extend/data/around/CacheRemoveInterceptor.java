package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheRemoveInterceptor implements XInterceptor {
    @Override
    public Object doIntercept(Object obj, MethodHolder mH, Object[] args, XInterceptorChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCacheRemove anno = mH.getAnnotation(XCacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, mH.getMethod(), mH.getParameters(), args);

        return tmp;
    }
}
