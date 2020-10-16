package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCache;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheInterceptor implements XInterceptor {
    @Override
    public Object doIntercept(Object obj, Object[] args, XInterceptorChain chain) throws Throwable {
        XCache anno = chain.method().getAnnotation(XCache.class);

        return CacheExecutorImp.global
                .cache(anno, chain.method().getMethod(), chain.method().getParameters(), args,
                        () -> chain.doIntercept(obj, args));
    }
}
