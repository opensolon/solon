package org.noear.solon.test.data;

import org.noear.solon.Utils;
import org.noear.solon.core.ValHolder;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;
import org.noear.solon.test.annotation.TestRollback;

/**
 * 回滚拦截器
 *
 * @author noear
 * @since 1.10
 */
public class TestRollbackInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        ValHolder val0 = new ValHolder();

        TestRollback anno0 = inv.method().getAnnotation(TestRollback.class);
        TranAnno anno1 = new TranAnno();

        if (anno0 != null) {
            anno1.policy(anno0.policy());
            anno1.readOnly(anno0.readOnly());
            anno1.isolation(anno0.isolation());
        }

        rollbackDo(anno1, () -> {
            val0.value = inv.invoke();
        });

        return val0.value;
    }


    /**
     * 回滚事务
     */
    public static void rollbackDo(RunnableEx runnable) throws Throwable {
        rollbackDo(null, runnable);
    }

    /**
     * 回滚事务
     */
    public static void rollbackDo(Tran tran, RunnableEx runnable) throws Throwable {
        if (tran == null) {
            tran = new TranAnno();
        }

        try {
            TranUtils.execute(tran, () -> {
                runnable.run();
                throw new TestRollbackException();
            });
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof TestRollbackException == false) {
                throw e;
            }
        }
    }
}
