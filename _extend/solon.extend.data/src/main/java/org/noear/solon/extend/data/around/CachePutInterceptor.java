package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCachePut;
import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodInterceptor;
import org.noear.solon.core.MethodHolder;
import org.noear.solon.extend.data.CacheExecutorImp;

public class CachePutInterceptor implements MethodInterceptor {

    @Override
    public Object doIntercept(Object obj, MethodHolder methodH, Object[] args, MethodChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCachePut anno = methodH.getAnnotation(XCachePut.class);
        CacheExecutorImp.global
                .cachePut(anno, methodH.getMethod(), methodH.getParameters(), args, tmp);

        return tmp;
    }
}
