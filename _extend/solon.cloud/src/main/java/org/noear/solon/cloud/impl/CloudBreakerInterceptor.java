package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.core.handle.InterceptorChain;
import org.noear.solon.ext.DataThrowable;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Object target, Object[] args, InterceptorChain chain) throws Throwable {
        CloudBreaker breaker = chain.method().getAnnotation(CloudBreaker.class);

        if (CloudClient.breaker() == null) {
            throw new IllegalArgumentException("Missing CloudBreakerService component");
        }

        if (breaker != null) {
            try (AutoCloseable entry = CloudClient.breaker().entry(breaker.value())) {
                return chain.doIntercept(target, args);
            } catch (BreakerException ex) {
                Context ctx = Context.current();
                if (ctx != null) {
                    ctx.statusSet(429);
                    ctx.setHandled(true);
                    throw new DataThrowable(ex);
                }else {
                    throw ex;
                }
            }
        } else {
            return chain.doIntercept(target, args);
        }
    }
}
