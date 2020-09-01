package org.noear.solon.extend.data;

import org.noear.solon.core.TranPolicy;
import org.noear.solon.ext.RunnableEx;

public class Trans {
    /**
     * 发起事务组
     * */
    public static void group(RunnableEx runnable) throws Throwable {
        TranExecutorImp.global.execute(TranMeta.TRAN_GROUP, runnable);
    }

    /**
     * 发起事务
     * */
    public static void tran(String name, RunnableEx runnable) throws Throwable {
        TranExecutorImp.global.execute(TranMeta.of(TranPolicy.required, name, false), runnable);
    }

    /**
     * 发起新的事务
     * */
    public static void tranNew(String name, RunnableEx runnable) throws Throwable {
        TranExecutorImp.global.execute(TranMeta.of(TranPolicy.requires_new, name, false), runnable);
    }

    /**
     * 不使用事务
     * */
    public static void not(RunnableEx runnable) throws Throwable {
        TranExecutorImp.global.execute(TranMeta.TRAN_NOT, runnable);
    }
}
