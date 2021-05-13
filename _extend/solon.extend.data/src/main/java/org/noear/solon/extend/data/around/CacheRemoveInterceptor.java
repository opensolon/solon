package org.noear.solon.extend.data.around;

import org.noear.solon.core.handle.Invocation;
import org.noear.solon.extend.data.annotation.CacheRemove;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CacheRemoveInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Object tmp = inv.invoke();

        CacheRemove anno = inv.method().getAnnotation(CacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, inv.method().getMethod(), inv.method().getParamWraps(), inv.args());

        return tmp;
    }
}
