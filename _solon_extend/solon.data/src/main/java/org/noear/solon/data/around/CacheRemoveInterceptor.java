package org.noear.solon.data.around;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.cache.CacheExecutorImp;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.core.aspect.Interceptor;

/**
 * 缓存移除拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class CacheRemoveInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Object tmp = inv.invoke();

        CacheRemove anno = inv.method().getAnnotation(CacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, inv, tmp);

        return tmp;
    }
}
