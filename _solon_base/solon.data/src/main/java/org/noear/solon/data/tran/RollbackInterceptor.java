package org.noear.solon.data.tran;

import org.noear.solon.core.ValHolder;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.data.annotation.Rollback;
import org.noear.solon.data.annotation.TranAnno;

/**
 * 回滚拦截器
 *
 * @author noear
 * @since 1.10
 */
public class RollbackInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        ValHolder val0 = new ValHolder();

        Rollback anno0 = inv.method().getAnnotation(Rollback.class);
        TranAnno anno1 = new TranAnno();

        if (anno0 != null) {
            anno1.policy(anno0.policy());
            anno1.readOnly(anno0.readOnly());
            anno1.isolation(anno0.isolation());
        }

        TranUtils.rollback(anno1, () -> {
            val0.value = inv.invoke();
        });

        return val0.value;
    }
}
