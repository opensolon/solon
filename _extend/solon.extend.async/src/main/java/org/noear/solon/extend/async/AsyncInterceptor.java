package org.noear.solon.extend.async;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.extend.async.annotation.Async;

/**
 * 异步执行拦截器
 *
 * @author noear
 * @since 1.6
 */
public class AsyncInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Async anno = inv.method().getAnnotation(Async.class);

        if (anno != null) {
            AsyncManager.submit(new InvocationRunnable(inv));
            return null;
        } else {
            return inv.invoke();
        }
    }
}
