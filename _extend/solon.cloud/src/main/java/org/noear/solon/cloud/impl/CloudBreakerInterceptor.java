package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
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
    public static final CloudBreakerInterceptor instance = new CloudBreakerInterceptor();

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (CloudClient.breaker() == null) {
            throw new IllegalArgumentException("Missing CloudBreakerService component");
        }

        CloudBreaker anno = inv.method().getAnnotation(CloudBreaker.class);

        if (anno != null) {
            //支持${xxx}配置
            String name = Solon.cfg().getByParse(Utils.annoAlias(anno.value(), anno.name()));

            try (AutoCloseable entry = CloudClient.breaker().entry(name)) {
                return inv.invoke();
            } catch (BreakerException ex) {
                Context ctx = Context.current();
                if (ctx != null) {
                    ctx.status(429);
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
