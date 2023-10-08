package org.noear.solon.scheduling.async;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * 异步执行拦截器
 *
 * @author noear
 * @since 1.11
 */
public class AsyncInterceptor implements Interceptor {
    AsyncExecutor asyncExecutor = new AsyncExecutorDefault();

    public AsyncInterceptor(AppContext context) {
        context.getBeanAsync(AsyncExecutor.class, bean -> {
            asyncExecutor = bean;
        });
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Async anno = inv.getMethodAnnotation(Async.class);

        if (anno != null) {
            Future future = asyncExecutor.submit(inv, anno);

            if (inv.method().getReturnType().isAssignableFrom(Future.class)) {
                return future;
            } else {
                return null;
            }
        } else {
            return inv.invoke();
        }
    }
}
