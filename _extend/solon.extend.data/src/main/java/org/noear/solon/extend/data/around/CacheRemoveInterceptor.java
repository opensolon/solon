package org.noear.solon.extend.data.around;

import org.noear.solon.extend.data.annotation.CacheRemove;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheRemoveInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Object obj, Object[] args, InterceptorChain chain) throws Throwable {
        Object tmp = chain.doIntercept(obj, args);

        CacheRemove anno = chain.method().getAnnotation(CacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, chain.method().getMethod(), chain.method().getParamWraps(), args);

        return tmp;
    }
}
