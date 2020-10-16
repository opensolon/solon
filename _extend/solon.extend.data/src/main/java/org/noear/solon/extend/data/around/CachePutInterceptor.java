package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCachePut;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CachePutInterceptor implements XInterceptor {

    @Override
    public Object doIntercept(Object obj, Object[] args, XInterceptorChain chain) throws Throwable {
        Object tmp = chain.doIntercept(obj, args);

        XCachePut anno = chain.method().getAnnotation(XCachePut.class);
        CacheExecutorImp.global
                .cachePut(anno, chain.method().getMethod(), chain.method().getParameters(), args, tmp);

        return tmp;
    }
}
