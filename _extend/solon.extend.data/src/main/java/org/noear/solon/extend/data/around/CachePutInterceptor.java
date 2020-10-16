package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCachePut;
import org.noear.solon.core.XInterceptorChain;
import org.noear.solon.core.XInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CachePutInterceptor implements XInterceptor {

    @Override
    public Object doIntercept(Object obj, MethodHolder methodH, Object[] args, XInterceptorChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCachePut anno = methodH.getAnnotation(XCachePut.class);
        CacheExecutorImp.global
                .cachePut(anno, methodH.getMethod(), methodH.getParameters(), args, tmp);

        return tmp;
    }
}
