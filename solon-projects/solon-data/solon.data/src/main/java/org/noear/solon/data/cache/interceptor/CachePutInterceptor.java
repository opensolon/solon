package org.noear.solon.data.cache.interceptor;

import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.CachePut;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.data.cache.CacheExecutorImp;

/**
 * 缓存更新拦截器
 *
 * @author noear
 * @since 1.0
 * */
public class CachePutInterceptor implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        //支持动态开关缓存
        if (Solon.app().enableCaching()) {
            Object tmp = inv.invoke();

            CachePut anno = inv.getMethodAnnotation(CachePut.class);
            CacheExecutorImp.global
                    .cachePut(anno, inv, tmp);

            return tmp;
        } else {
            return inv.invoke();
        }
    }
}
