package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.model.BreakerException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.ext.DataThrowable;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        CloudBreaker breaker = inv.method().getAnnotation(CloudBreaker.class);

        if (CloudClient.breaker() == null) {
            throw new IllegalArgumentException("Missing CloudBreakerService component");
        }

        if (breaker != null) {
            try (AutoCloseable entry = CloudClient.breaker().entry(breaker.value())) {
                return inv.invoke();
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
            return inv.invoke();
        }
    }
}
