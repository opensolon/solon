package org.noear.solon.scheduling.retry;

import org.noear.solon.Solon;
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
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Retry anno = inv.method().getAnnotation(Retry.class);

        if (anno != null) {
            return RetryableTask.of(() -> {
                        try {
                            return inv.method().getMethod().invoke(inv.target(), inv.args());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .maxRetryCount(anno.maxAttempts())
                    .interval(anno.interval())
                    .unit(anno.unit())
                    .recover(() -> {
                        return Solon.context().getBeanOrNew(anno.recover()).recover();
                    })
                    .retryForExceptions(anno.exs())
                    .execute()
                    .get();
        } else {
            return inv.invoke();
        }

    }
}
