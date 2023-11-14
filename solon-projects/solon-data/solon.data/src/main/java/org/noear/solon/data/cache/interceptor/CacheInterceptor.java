package org.noear.solon.data.cache.interceptor;

import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.cache.CacheExecutorImp;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.core.aspect.Interceptor;

/**
 * 缓存拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class CacheInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //支持动态开关缓存
        if (Solon.app().enableCaching()) {
            Cache anno = inv.getMethodAnnotation(Cache.class);

            return CacheExecutorImp.global
                    .cache(anno, inv, () -> inv.invoke());
        } else {
            return inv.invoke();
        }
    }
}
