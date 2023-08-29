package io.seata.solon.impl;

import io.seata.rm.GlobalLockTemplate;
import io.seata.solon.annotation.GlobalLock;
import io.seata.solon.impl.GlobalLockExecutorImpl;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 2.4
 */
public class GlobalLockInterceptor implements Interceptor {
    GlobalLockTemplate globalLockTemplate;

    public GlobalLockInterceptor(GlobalLockTemplate globalLockTemplate) {
        this.globalLockTemplate = globalLockTemplate;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        GlobalLock anno = inv.method().getAnnotation(GlobalLock.class);

        if (anno == null) {
            return null;
        } else {
            return globalLockTemplate.execute(new GlobalLockExecutorImpl(inv, anno));
        }
    }
}
