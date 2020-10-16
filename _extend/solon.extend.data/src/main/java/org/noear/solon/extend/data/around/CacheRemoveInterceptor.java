package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheRemoveInterceptor implements MethodInterceptor {
    @Override
    public Object doIntercept(Object obj, MethodHolder mH, Object[] args, MethodChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCacheRemove anno = mH.getAnnotation(XCacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, mH.getMethod(), mH.getParameters(), args);

        return tmp;
    }
}
