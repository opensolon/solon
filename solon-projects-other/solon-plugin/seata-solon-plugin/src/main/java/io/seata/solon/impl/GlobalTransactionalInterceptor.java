package io.seata.solon.impl;

import io.seata.solon.annotation.GlobalTransactional;
import io.seata.tm.api.TransactionalTemplate;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 2.5
 */
public class GlobalTransactionalInterceptor implements Interceptor {
    TransactionalTemplate transactionalTemplate;

    public GlobalTransactionalInterceptor(TransactionalTemplate transactionalTemplate) {
        this.transactionalTemplate = transactionalTemplate;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        GlobalTransactional anno = inv.method().getAnnotation(GlobalTransactional.class);
        if (anno == null) {
            anno = inv.method().getDeclaringClzAnnotation(GlobalTransactional.class);
        }

        if (anno == null) {
            return inv.invoke();
        } else {
            return transactionalTemplate.execute(new GlobalTransactionalExecutorImpl(inv, anno));
        }
    }
}
