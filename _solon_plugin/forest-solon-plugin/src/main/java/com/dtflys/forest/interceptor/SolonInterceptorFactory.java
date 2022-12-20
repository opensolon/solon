package com.dtflys.forest.interceptor;

import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 1.11
 */
public class SolonInterceptorFactory extends DefaultInterceptorFactory {
    final AopContext context;
    public SolonInterceptorFactory(AopContext context){
        this.context = context;
    }

    @Override
    protected <T extends Interceptor> Interceptor createInterceptor(Class<T> clazz) {
        Interceptor interceptor = null;
        try {
            interceptor = context.getBeanOrNew(clazz);
        } catch (Throwable th) {
        }

        if (interceptor != null) {
            interceptorMap.put(clazz, interceptor);
        } else {
            return super.createInterceptor(clazz);
        }

        return interceptor;
    }
}
