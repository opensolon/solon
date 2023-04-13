package org.noear.solon.scheduling.async;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.Async;

/**
 * 异步执行拦截器
 *
 * @author noear
 * @since 1.11
 */
public class AsyncInterceptor implements Interceptor {
    InvocationRunnableFactory runnableFactory;

    public AsyncInterceptor(AopContext context) {
        context.getBeanAsync(InvocationRunnableFactory.class, bean -> {
            runnableFactory = bean;
        });
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Async anno = inv.method().getAnnotation(Async.class);

        if (anno != null) {
            Runnable runnable = createRunnable(inv);
            if (runnable != null) {
                RunUtil.async(runnable);
            }

            return null;
        } else {
            return inv.invoke();
        }
    }

    private Runnable createRunnable(Invocation inv) {
        if (runnableFactory == null) {
            return new InvocationRunnable(inv);
        } else {
            return runnableFactory.create(inv);
        }
    }
}
