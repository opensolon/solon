package org.noear.solon.extend.data.around;

import org.noear.solon.annotation.XCache;
import org.noear.solon.core.InvokeChain;
import org.noear.solon.core.InvokeHandler;
import org.noear.solon.core.XBridge;
import org.noear.solon.extend.data.CacheExecutorImp;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class CacheInvokeHandler implements InvokeHandler {
    @Override
    public Object doInvoke(Object obj, Method method, Parameter[] parameters, Object[] args, InvokeChain invokeChain) throws Throwable {
        XCache anno = method.getAnnotation(XCache.class);

        return CacheExecutorImp.global
                .cache(anno, method, parameters, args,
                        () -> invokeChain.doInvoke(obj, args));
    }
}
