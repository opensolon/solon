package org.noear.solon.extend.data;

import org.noear.solon.annotation.XTran;
import org.noear.solon.core.*;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.trans.DbTran;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

/**
 * 事务管理
 * */
public class TranExecutorImp implements XTranExecutor {
    public static final TranExecutorImp global = new TranExecutorImp();

    protected TranExecutorImp() {

    }


    private ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();

    public TranFactory factory() {
        return TranFactory.singleton();
    }

    @Override
    public boolean inTrans() {
        return TranManager.current() != null;
    }

    @Override
    public boolean inTransAndReadOnly() {
        DbTran tran = TranManager.current();
        return tran != null && tran.getMeta().readOnly();
    }

    @Override
    public Connection getConnection(DataSource ds) throws SQLException {
        DbTran tran = TranManager.current();

        if (tran == null) {
            return ds.getConnection();
        } else {
            return tran.getConnection(ds);
        }
    }

    @Override

    public void execute(XTran meta, RunnableEx runnable) throws Throwable {
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

    private void forRoot(Stack<TranEntity> stack, XTran meta, RunnableEx runnable) throws Throwable {
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

    private void forNotRoot(Stack<TranEntity> stack, XTran meta, RunnableEx runnable) throws Throwable {
        //获取上一个事务
        TranEntity before = stack.peek();

        //1.
        if (meta.policy() == TranPolicy.supports) {
            runnable.run();
            return;
        }

        //2.当前：排除 或 绝不 （不需要加入事务组）//不需要入栈
        if (meta.policy() == TranPolicy.not_supported
                || meta.policy() == TranPolicy.never) {
            factory().create(meta).apply(runnable);
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
            factory().create(meta).apply(runnable);
        }

        if (meta.policy() != TranPolicy.nested) {
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


    private void apply2(Stack<TranEntity> stack, Tran tran, XTran meta, RunnableEx runnable) throws Throwable {
        if (meta.policy().code <= TranPolicy.nested.code) {
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
