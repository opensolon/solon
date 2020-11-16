package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCachePut;
import org.noear.solon.core.handler.InterceptorChain;
import org.noear.solon.core.handler.Interceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CachePutInterceptor implements Interceptor {

    @Override
    public Object doIntercept(Object obj, Object[] args, InterceptorChain chain) throws Throwable {
        Object tmp = chain.doIntercept(obj, args);

        XCachePut anno = chain.method().getAnnotation(XCachePut.class);
        CacheExecutorImp.global
                .cachePut(anno, chain.method().getMethod(), chain.method().getParameters(), args, tmp);

        return tmp;
    }
}
