package org.noear.solon.scheduling.retry;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.scheduling.annotation.Retry;

/**
 * 重试方法拦截器
 *
 * @author kongweiguang
 * @since 2.3
 */
public class RetryInterceptor implements Interceptor {
    AppContext appContext;

    public RetryInterceptor(AppContext aopContext) {
        this.appContext = aopContext;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Retry anno = inv.getMethodAnnotation(Retry.class);

        if (anno != null) {
            Callee callee = new CalleeImpl(inv);
            Recover recover = appContext.getBeanOrNew(anno.recover());

            return RetryableTask.of(callee)
                    .maxRetryCount(anno.maxAttempts())
                    .interval(anno.interval())
                    .unit(anno.unit())
                    .recover(recover)
                    .retryForIncludes(anno.value())
                    .retryForIncludes(anno.include())
                    .retryForExcludes(anno.exclude())
                    .execute()
                    .get();
        } else {
            return inv.invoke();
        }
    }
}
