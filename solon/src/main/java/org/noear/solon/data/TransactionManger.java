package org.noear.solon.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

import java.util.function.Function;

/**
 * 事务管理器
 * */
public class TransactionManger {
    public static Function<XTran, Transaction> factory;
    private static ThreadLocal<Transaction> rootLocal = new ThreadLocal<>();

    public void execute(XTran anno, RunnableEx runnable) throws Throwable {
        if (anno == null) {
            runnable.run();
            return;
        }

        Transaction root = rootLocal.get();
        Transaction tran = factory.apply(anno);

        //根事务不存在
        if (root == null) {
            try {
                rootLocal.set(tran);
                tran.execute(runnable);
            } finally {
                rootLocal.remove();
            }
        } else {
            //根事务已经存在；如果是主事务，则加入
            if (root.isMaster()) {
                root.add(tran);
            }

            tran.execute(runnable);
        }
    }
}
