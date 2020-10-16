package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCache;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheInterceptor implements XInterceptor {
    @Override
    public Object doIntercept(Object obj, MethodHolder mH, Object[] args, XInterceptorChain invokeChain) throws Throwable {
        XCache anno = mH.getAnnotation(XCache.class);

        return CacheExecutorImp.global
                .cache(anno, mH.getMethod(), mH.getParameters(), args,
                        () -> invokeChain.doInvoke(obj, args));
    }
}
