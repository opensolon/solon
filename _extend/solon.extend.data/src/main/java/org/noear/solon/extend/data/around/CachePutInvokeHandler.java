package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCacheUpdate;
import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodHandler;
import org.noear.solon.extend.data.CacheExecutorImp;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CachePutInvokeHandler implements MethodHandler {
    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] parameters, Object[] args, MethodChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCacheUpdate anno = method.getAnnotation(XCacheUpdate.class);
        CacheExecutorImp.global
                .cacheUpdate(anno, method, parameters, args, tmp);

        return tmp;
    }
}
