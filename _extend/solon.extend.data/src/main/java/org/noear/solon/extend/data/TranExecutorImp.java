package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;

import java.util.Stack;

/**
 * 事务管理
 * */
public class TranExecutorImp implements TranExecutor {
    public static final TranExecutor global = new TranExecutorImp();

    protected TranExecutorImp(){

    }


    private ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();

    public TranFactory factory() {
        return XBridge.tranFactory();
    }

    public void execute(XTran anno, RunnableEx runnable) throws Throwable {
        if (anno == null || factory() == null) {
            //
            //如果没有注解或工厂，直接运行
            //
            runnable.run();
            return;
        }

        Stack<TranEntity> stack = local.get();

        //根事务不存在
        if (stack == null) {
            forRoot(stack, anno, runnable);
        } else {
            forNotRoot(stack, anno, runnable);
        }
    }

    private void forRoot(Stack<TranEntity> stack, XTran anno, RunnableEx runnable) throws Throwable {
        //::支持但不必需 或排除 或决不
        if (anno.policy() == TranPolicy.supports
                || anno.policy() == TranPolicy.not_supported
                || anno.policy() == TranPolicy.never) {
            runnable.run();
            return;
        } else {
            //新建事务，并置为根事务
            Tran tran = factory().create(anno);
            stack = new Stack<>();

            try {
                local.set(stack);
                apply2(stack, tran, anno, runnable);
            } finally {
                local.remove();
            }
        }
    }

    private void forNotRoot(Stack<TranEntity> stack, XTran anno, RunnableEx runnable) throws Throwable {
        //获取之前的事务
        TranEntity before = stack.peek();

        if (anno.policy() == TranPolicy.supports) {
            if (anno.value().equals(before.anno.value()) //当前为同源
                    || before.anno.group()) { //或，当前为组
                //直接运行，即并入
                runnable.run();
            } else {
                //挂起
                factory().pending(runnable);
            }
            return;
        }

        //当前：排除 或 绝不 （不需要加入事务组）//不需要入栈
        if (anno.policy() == TranPolicy.not_supported
                || anno.policy() == TranPolicy.never) {
            factory().create(anno).apply(runnable);
            return;
        }

        //当前：事务组 新起事务且不需要加入上个事务组 //入栈，供后来事务用
        if (anno.group()) {
            if (before.anno.group()) {
                runnable.run();
            } else {
                Tran tran = factory().create(anno);
                apply2(stack, tran, anno, runnable);
            }
            return;
        }

        //当前：事务组 或 新建 或嵌套；新起事务且不需要加入上个事务组 //入栈，供后来事务用
        if (anno.policy() == TranPolicy.requires_new) {
            Tran tran = factory().create(anno);
            apply2(stack, tran, anno, runnable);
            return;
        }

        //当前：必须有同源事务
        if (anno.policy() == TranPolicy.mandatory) {
            if (anno.value().equals(before.anno.value())) {
                Tran tran = factory().create(anno);
                tran.apply(runnable);
            } else {
                throw new RuntimeException("You must have the same source transaction");
            }
            return;
        }


        if (before.tran.isGroup()) {
            //如果之前的是事务组，则新建事务加入访事务组  //入栈，供后来事务用
            //
            Tran tran = factory().create(anno);
            before.tran.add(tran);

            apply2(stack, tran, anno, runnable);
            return;
        } else {
            //如果之前不是事务组
            //
            if (before.anno.value().equals(anno.value())
                    && anno.policy() != TranPolicy.nested) {
                //如果同源 并且不嵌套，则直接并入
                runnable.run();
            } else {
                //不同源 或嵌套；则新建事务（不同源，嵌套可能会有问题） //入栈，供后来事务用
                Tran tran = factory().create(anno);
                apply2(stack, tran, anno, runnable);
            }
        }
    }


    private void apply2(Stack<TranEntity> stack, Tran tran, XTran anno, RunnableEx runnable) throws Throwable {
        if (anno.group() || anno.policy().code <= TranPolicy.nested.code) {
            //@group || required || requires_new || nested ，需要入栈
            //
            try {
                //入栈
                stack.push(new TranEntity(tran, anno));
                tran.apply(runnable);
            } finally {
                //出栈
                stack.pop();
            }
        } else {
            //不需要入栈
            //
            tran.apply(runnable);
        }
    }
}
