package org.noear.solon.scheduling.async;

import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.Async;
import org.noear.solon.scheduling.annotation.EnableAsync;

/**
 * 异步执行拦截器
 *
 * @author noear
 * @since 1.11
 */
public class AsyncInterceptor implements Interceptor {
    boolean enableAsync;

    IAsyncInvocationRunnableFactory runnableFactory;

    public AsyncInterceptor() {
        enableAsync = Solon.app().source().getAnnotation(EnableAsync.class) != null;
        if (enableAsync) {
            Solon.context().getBeanAsync(IAsyncInvocationRunnableFactory.class, it -> {
                runnableFactory = it;
            });
        }
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (enableAsync) {
            Async anno = inv.method().getAnnotation(Async.class);

            if (anno != null) {
                Runnable runnable = createAsyncRunnable(inv);
                RunUtil.async(runnable);
                return null;
            } else {
                return inv.invoke();
            }
        } else {
            return inv.invoke();
        }
    }

    private Runnable createAsyncRunnable(Invocation inv) {
        if (runnableFactory == null) {
            runnableFactory = new IAsyncInvocationRunnableFactory() {
                @Override
                public Runnable create(Invocation inv) {
                    return new AsyncInvocationRunnable(inv);
                }
            };
        }

        return runnableFactory.create(inv);
    }
}
