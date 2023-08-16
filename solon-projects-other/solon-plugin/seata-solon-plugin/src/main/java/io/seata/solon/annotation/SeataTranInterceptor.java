package io.seata.solon.annotation;

import io.seata.solon.impl.TransactionalExecutorImpl;
import io.seata.tm.api.TransactionalTemplate;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

/**
 * @author noear
 * @since 2.4
 */
public class SeataTranInterceptor implements Interceptor {
    TransactionalTemplate transactionalTemplate;

    public SeataTranInterceptor(TransactionalTemplate transactionalTemplate){
        this.transactionalTemplate = transactionalTemplate;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        SeataTran anno = inv.method().getAnnotation(SeataTran.class);
        if (anno == null) {
            return inv.invoke();
        } else {
            return transactionalTemplate.execute(new TransactionalExecutorImpl(inv, anno));
        }
    }
}
