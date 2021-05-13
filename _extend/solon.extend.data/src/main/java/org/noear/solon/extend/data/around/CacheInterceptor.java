package org.noear.solon.extend.data.around;

import org.noear.solon.core.handle.Invocation;
import org.noear.solon.extend.data.annotation.Cache;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Cache anno = inv.method().getAnnotation(Cache.class);

        return CacheExecutorImp.global
                .cache(anno, inv.method().getMethod(), inv.method().getParamWraps(), inv.args(),
                        () -> inv.invoke());
    }
}
