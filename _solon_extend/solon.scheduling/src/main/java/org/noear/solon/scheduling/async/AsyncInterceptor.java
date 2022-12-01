package org.noear.solon.scheduling.async;

import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.scheduling.annotation.Async;

/**
 * 异步执行拦截器
 *
 * @author noear
 * @since 1.11
 */
public class AsyncInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Async anno = inv.method().getAnnotation(Async.class);

        if (anno != null) {
            Utils.async(new AsyncInvocationRunnable(inv));
            return null;
        } else {
            return inv.invoke();
        }
    }
}
