package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;

import java.util.Stack;

/**
 * 事务管理
 * */
public class TranExecutorImp implements TranExecutor {
    public static final TranExecutorImp global = new TranExecutorImp();

    protected TranExecutorImp() {

    }


    private ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();

    public TranFactory factory() {
        return TranFactory.singleton();
    }

    public void execute(XTran anno, RunnableEx runnable) throws Throwable {
        if (anno == null || factory() == null) {
            //
            //如果没有注解或工厂，直接运行
            //
            runnable.run();
        } else {
            execute(TranMeta.of(anno), runnable);
        }
    }

    void execute(TranMeta meta, RunnableEx runnable) throws Throwable {
        if (meta == null || factory() == null) {
            //
            //如果没有注解或工厂，直接运行
            //
            runnable.run();
            return;
        }

        Stack<TranEntity> stack = local.get();

        //根事务不存在
        if (stack == null) {
            forRoot(stack, meta, runnable);
        } else {
            forNotRoot(stack, meta, runnable);
        }
    }

    private void forRoot(Stack<TranEntity> stack, TranMeta meta, RunnableEx runnable) throws Throwable {
        //::支持但不必需 或排除 或决不
        if (meta.policy() == TranPolicy.supports
                || meta.policy() == TranPolicy.not_supported
                || meta.policy() == TranPolicy.never) {
            runnable.run();
            return;
        } else {
            //新建事务，并置为根事务
            Tran tran = factory().create(meta);
            stack = new Stack<>();

            try {
                local.set(stack);
                apply2(stack, tran, meta, runnable);
            } finally {
                local.remove();
            }
        }
    }

    private void forNotRoot(Stack<TranEntity> stack, TranMeta meta, RunnableEx runnable) throws Throwable {
        //获取上一个事务
        TranEntity before = stack.peek();

        //1.
        if (meta.policy() == TranPolicy.supports) {
            if (before.meta.group() //当前为组
                    || meta.name().equals(before.meta.name())) { //或，当前为同源
                //直接运行，即并入
                runnable.run();
            } else {
                //挂起（即不使用事务）
                factory().pending(runnable);
            }
            return;
        }

        //2.当前：排除 或 绝不 （不需要加入事务组）//不需要入栈
        if (meta.policy() == TranPolicy.not_supported
                || meta.policy() == TranPolicy.never) {
            factory().create(meta).apply(runnable);
            return;
        }

        //3.当前：事务组 //入栈，供后来事务用
        if (meta.group()) {
            if (before.meta.group()) { //如果之前也是组，则并入
                runnable.run();
            } else {
                Tran tran = factory().create(meta);
                //尝试加入上个事务***
                before.tran.add(tran);
                apply2(stack, tran, meta, runnable);
            }
            return;
        }

        //4.当前：新起事务且不需要加入上个事务 //入栈，供后来事务用
        if (meta.policy() == TranPolicy.requires_new) {
            Tran tran = factory().create(meta);
            apply2(stack, tran, meta, runnable);
            return;
        }

        //5.当前：必须有同源事务
        if (meta.policy() == TranPolicy.mandatory) {
            if (meta.name().equals(before.meta.name())) {
                Tran tran = factory().create(meta);
                tran.apply(runnable);
            } else {
                throw new RuntimeException("You must have the same source transaction");
            }
            return;
        }


        if (before.meta.group()) {
            //如果之前的是事务组，则新建事务加入访事务组  //入栈，供后来事务用
            //
            Tran tran = factory().create(meta);
            //尝试加入上个事务***
            before.tran.add(tran);

            apply2(stack, tran, meta, runnable);
            return;
        } else {
            //如果之前不是事务组
            //
            if (before.meta.name().equals(meta.name())
                    && meta.policy() != TranPolicy.nested) {
                //如果同源 并且不嵌套，则直接并入
                runnable.run();
            } else {
                //不同源 或嵌套；则新建事务（不同源，嵌套可能会有问题） //入栈，供后来事务用
                Tran tran = factory().create(meta);

                //尝试加入上个事务***
                before.tran.add(tran);

                apply2(stack, tran, meta, runnable);
            }
        }
    }


    private void apply2(Stack<TranEntity> stack, Tran tran, TranMeta meta, RunnableEx runnable) throws Throwable {
        if (meta.group() || meta.policy().code <= TranPolicy.nested.code) {
            //@group || required || requires_new || nested ，需要入栈
            //
            try {
                //入栈
                stack.push(new TranEntity(tran, meta));
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
