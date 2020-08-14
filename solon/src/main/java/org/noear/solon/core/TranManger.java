package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

/**
 * 事务管理器
 * */
public class TranManger {
    private static TranFactory factory;
    private static ThreadLocal<ValHolder<Tran>> rootLocal = new ThreadLocal<>();

    public static void setFactory(TranFactory factory) {
        TranManger.factory = factory;
    }

    public static void execute(XTran anno, RunnableEx runnable) throws Throwable {
        if (anno == null || factory == null) {
            runnable.run();
            return;
        }

        ValHolder<Tran> root = rootLocal.get();


        //根事务不存在
        if (root == null) {
            //::支持但不必需
            if(anno.policy() == TranPolicy.supports){
                runnable.run();
            }else {
                //新建事务
                Tran tran = factory.create(anno);

                ValHolder<Tran> vh = new ValHolder<>();
                vh.value = tran;
                vh.tag = anno.value();

                try {
                    rootLocal.set(vh);
                    tran.execute(runnable);
                } finally {
                    rootLocal.remove();
                }
            }
        } else {
            //根事务已经存在
            if (root.value.isQueue()) {
                //如果是主事务，则加入
                //
                Tran tran = factory.create(anno);

                root.value.add(tran);
                tran.execute(runnable);
                return;
            }

            if(root.tag.equals(anno.value())){
                //如果名字相同，则直接执行
                runnable.run();
            }else{
                //新建事务（不同数据源的事务嵌套，会有潜在问题）
                Tran tran = factory.create(anno);
                tran.execute(runnable);
            }
        }
    }
}
