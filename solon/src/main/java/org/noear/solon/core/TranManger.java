package org.noear.solon.core;

import org.noear.solon.annotation.XTran;
import org.noear.solon.ext.RunnableEx;

import java.util.Stack;

/**
 * 事务管理
 * */
public class TranManger {
    private static TranFactory factory;
    private static ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();

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

        Stack<TranEntity> stack = local.get();


        //根事务不存在
        if (stack == null) {
            //::支持但不必需 或排除 或决不
            if (anno.policy() == TranPolicy.supports
                    || anno.policy() == TranPolicy.exclude
                    || anno.policy() == TranPolicy.never) {
                runnable.run();
                return;
            } else {
                //新建事务，并置为根事务
                Tran tran = factory.create(anno);
                stack = new Stack<>();

                try {
                    local.set(stack);
                    apply2(stack, tran, anno, runnable);
                } finally {
                    local.remove();
                }
            }
        } else {
            if(anno.policy() == TranPolicy.supports){
                runnable.run();
                return;
            }

            //当前：排除 或 绝不 或必须 （不需要加入事务组）//不需要入栈
            if (anno.policy() == TranPolicy.exclude
                    || anno.policy() == TranPolicy.mandatory
                    || anno.policy() == TranPolicy.never) {
                Tran tran = factory.create(anno);
                tran.apply(runnable);
                return;
            }



            //当前：事务组 或 新建 或嵌套；新起事务且不需要加入上个事务组 //入栈，供后来事务用
            if (anno.group() || anno.policy() == TranPolicy.requires_new) {
                Tran tran = factory.create(anno);
                apply2(stack, tran, anno, runnable);
                return;
            }


            //获取之前的事务
            TranEntity before = stack.peek();

            if (before.tran.isGroup()) {
                //如果之前的是事务组，则新建事务加入访事务组  //入栈，供后来事务用
                //
                Tran tran = factory.create(anno);

                before.tran.add(tran);

                apply2(stack, tran, anno, runnable);
                return;
            } else {
                //如果之前不是事务组
                //
                if (before.anno.value().equals(anno.value())
                        && anno.policy() == TranPolicy.nested) {
                    //如果同源 并且不嵌套，则直接并入
                    runnable.run();
                } else {
                    //不同源 或嵌套；则新建事务（不同源，嵌套可能会有问题） //入栈，供后来事务用
                    Tran tran = factory.create(anno);
                    apply2(stack, tran, anno, runnable);
                }
            }
        }
    }

    private static void apply2(Stack<TranEntity> stack, Tran tran, XTran anno, RunnableEx runnable) throws Throwable {
        try {
            //入栈
            stack.push(new TranEntity(tran, anno));
            tran.apply(runnable);
        } finally {
            //出栈
            stack.pop();
        }
    }
}
