package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCacheRemove;
import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodInterceptor;
import org.noear.solon.extend.data.CacheExecutorImp;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CacheRemoveInvokeHandler implements MethodInterceptor {
    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] parameters, Object[] args, MethodChain invokeChain) throws Throwable {
        Object tmp = invokeChain.doInvoke(obj, args);

        XCacheRemove anno = method.getAnnotation(XCacheRemove.class);
        CacheExecutorImp.global
                .cacheRemove(anno, method, parameters, args);

        return tmp;
    }
}
