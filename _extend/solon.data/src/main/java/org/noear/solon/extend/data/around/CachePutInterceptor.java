package org.noear.solon.extend.data.around;

import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.extend.data.annotation.CachePut;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CachePutInterceptor implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Object tmp = inv.invoke();

        CachePut anno = inv.method().getAnnotation(CachePut.class);
        CacheExecutorImp.global
                .cachePut(anno, inv.method().getMethod(), inv.method().getParamWraps(), inv.args(), tmp);

        return tmp;
    }
}
