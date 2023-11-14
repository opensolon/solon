package org.noear.solon.test.data;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.annotation.TranAnno;
import org.noear.solon.data.tran.TranUtils;
import org.noear.solon.test.annotation.Rollback;
import org.noear.solon.test.annotation.TestRollback;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 回滚拦截器
 *
 * @author noear
 * @since 1.10
 */
public class RollbackInterceptor implements Interceptor {
    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        if (Solon.app() == null) {
            //没有容器，没法运行事务回滚
            return inv.invoke();
        } else {
            AtomicReference val0 = new AtomicReference();

            Rollback anno0 = inv.getMethodAnnotation(Rollback.class);
            if (anno0 == null) {
                anno0 = new RollbackAnno(inv.getMethodAnnotation(TestRollback.class));
            }

            rollbackDo(anno0.value(), () -> {
                val0.set(inv.invoke());
            });

            return val0.get();
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackDo(boolean isRollback, RunnableEx runnable) throws Throwable {
        //备份
        boolean enableTransactionBak = Solon.app().enableTransaction();

        try {
            if (isRollback) {
                //应用（可能要改成开关控制）
                Solon.app().chainManager().addInterceptorIfAbsent(RollbackRouterInterceptor.getInstance(), Integer.MAX_VALUE);

                //当前
                TranUtils.execute(new TranAnno(), () -> {
                    runnable.run();
                    throw new RollbackException();
                });
            } else {
                //整体禁用（要改进禁用的控制泛型）
                Solon.app().enableTransaction(false);
            }

        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof RollbackException) {
                System.out.println("@Rollback: the transaction has been rolled back!");
            } else {
                throw e;
            }
        } finally {
            //恢复备份
            Solon.app().enableTransaction(enableTransactionBak);

            if (isRollback) {
                Solon.app().chainManager().removeInterceptor(RollbackRouterInterceptor.class);
            }
        }
    }
}
