package org.noear.solon.data.cache.interceptor;

import org.noear.solon.Solon;
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
        //支持动态开关缓存
        if (Solon.app().enableCaching()) {
            Object tmp = inv.invoke();

            CacheRemove anno = inv.getMethodAnnotation(CacheRemove.class);
            CacheExecutorImp.global
                    .cacheRemove(anno, inv, tmp);

            return tmp;
        } else {
            return inv.invoke();
        }
    }
}
