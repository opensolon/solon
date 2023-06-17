package org.noear.solon.scheduling.retry;

import org.noear.solon.core.AopContext;
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
    AopContext aopContext;

    public RetryInterceptor(AopContext aopContext) {
        this.aopContext = aopContext;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Retry anno = inv.method().getAnnotation(Retry.class);

        if (anno != null) {
            return RetryableTask.of(() -> inv.method().getMethod().invoke(inv.target(), inv.args()))
                    .maxRetryCount(anno.maxAttempts())
                    .interval(anno.interval())
                    .unit(anno.unit())
                    .recover(aopContext.getBeanOrNew(anno.recover()))
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
