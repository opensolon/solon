package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

/**
 * 事务管理
 * */
public class TranManger {
    private static TranFactory factory;
    private static ThreadLocal<TranEntity> rootLocal = new ThreadLocal<>();

    /**
     * 设置事务工厂
     */
    public static void setFactory(TranFactory factory) {
        TranManger.factory = factory;
    }

    public static void execute(XTran anno, RunnableEx runnable) throws Throwable {
        if (anno == null || factory == null) {
            //
            //如果没有注解或工厂，直接运行
            //
            runnable.run();
            return;
        }

        TranEntity root = rootLocal.get();


        //根事务不存在
        if (root == null) {
            //::支持但不必需 或排除 或决不
            if (anno.policy() == TranPolicy.supports
                    || anno.policy() == TranPolicy.exclude
                    || anno.policy() == TranPolicy.never) {
                runnable.run();
                return;
            } else {
                //新建事务，并置为根事务
                Tran tran = factory.create(anno);

                root = new TranEntity(tran,anno);

                try {
                    rootLocal.set(root);
                    tran.apply(runnable);
                } finally {
                    rootLocal.remove();
                }
            }
        } else {
            //事务排斥 或 全新事务（不需要加入事务组）
            if (anno.policy() == TranPolicy.exclude
                    || anno.policy() == TranPolicy.requires_new) {
                Tran tran = factory.create(anno);
                tran.apply(runnable);
                return;
            }

            if (root.tran.isGroup()) {
                //如果根是事务组，则新建事务加入事务组
                //
                Tran tran = factory.create(anno);

                root.tran.add(tran);
                tran.apply(runnable);
                return;
            } else {
                //如果根不是队列
                //
                if (root.anno.value().equals(anno.value())) {
                    //如果同源，则直接并入
                    runnable.run();
                } else {
                    //不同源；则新建事务（不同源，嵌套可能会有问题）
                    Tran tran = factory.create(anno);
                    tran.apply(runnable);
                }
            }
        }
    }
}
