package io.seata.solon.annotation;

import io.seata.rm.GlobalLockTemplate;
import io.seata.solon.impl.GlobalLockExecutorImpl;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 2.4
 */
public class SeataLockInterceptor implements Interceptor {
    GlobalLockTemplate globalLockTemplate;

    public SeataLockInterceptor(GlobalLockTemplate globalLockTemplate) {
        this.globalLockTemplate = globalLockTemplate;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        SeataLock anno = inv.method().getAnnotation(SeataLock.class);

        if (anno == null) {
            return null;
        } else {
            return globalLockTemplate.execute(new GlobalLockExecutorImpl(inv, anno));
        }
    }
}
