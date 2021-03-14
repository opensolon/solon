package org.noear.solon.cloud.impl;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.cloud.model.Entry;
import org.noear.solon.core.handle.Interceptor;
import org.noear.solon.core.handle.InterceptorChain;

/**
 * @author noear
 * @since 1.3
 */
public class CloudBreakerInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Object target, Object[] args, InterceptorChain chain) throws Throwable {
        CloudBreaker breaker = chain.method().getAnnotation(CloudBreaker.class);

        if (breaker != null) {
            try (Entry entry = CloudClient.breaker().entry(breaker.value())) {
                return chain.doIntercept(target, args);
            }
        } else {
            return chain.doIntercept(target, args);
        }
    }
}
