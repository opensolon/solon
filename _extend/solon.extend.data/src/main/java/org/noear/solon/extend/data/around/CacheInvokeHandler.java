package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCache;
import org.noear.solon.core.MethodChain;
import org.noear.solon.core.MethodHandler;
import org.noear.solon.extend.data.CacheExecutorImp;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CacheInvokeHandler implements MethodHandler {
    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] parameters, Object[] args, MethodChain invokeChain) throws Throwable {
        XCache anno = method.getAnnotation(XCache.class);

        return CacheExecutorImp.global
                .cache(anno, method, parameters, args,
                        () -> invokeChain.doInvoke(obj, args));
    }
}
