package org.noear.solon.extend.data.around;

import org.noear.solon.extend.data.annotation.Cache;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Object obj, Object[] args, InterceptorChain chain) throws Throwable {
        Cache anno = chain.method().getAnnotation(Cache.class);

        return CacheExecutorImp.global
                .cache(anno, chain.method().getMethod(), chain.method().getParamWraps(), args,
                        () -> chain.doIntercept(obj, args));
    }
}
