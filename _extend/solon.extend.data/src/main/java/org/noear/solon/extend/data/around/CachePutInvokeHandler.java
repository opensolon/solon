package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCachePut;
import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodHandler;
import org.noear.solon.extend.data.CacheExecutorImp;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CachePutInvokeHandler implements MethodHandler {
    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] parameters, Object[] args, MethodChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCachePut anno = method.getAnnotation(XCachePut.class);
        CacheExecutorImp.global
                .cacheUpdate(anno, method, parameters, args, tmp);

        return tmp;
    }
}
