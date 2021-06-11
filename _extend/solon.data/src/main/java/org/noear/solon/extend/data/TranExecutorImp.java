package org.noear.solon.extend.data;

import org.noear.solon.extend.data.annotation.Tran;
import org.noear.solon.core.tran.TranExecutor;
import org.noear.solon.extend.data.tran.TranPolicy;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.data.tranImp.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

/**
 * 事务执行器
 *
 * 基于 节点 与 栈管理
 * */
public class TranExecutorImp implements TranExecutor {
    public static final TranExecutorImp global = new TranExecutorImp();

    protected TranExecutorImp() {

    }

    protected ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();

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

    protected TranNode tranNot = new TranNotImp();
    protected TranNode tranNever = new TranNeverImp();
    protected TranNode tranMandatory = new TranMandatoryImp();

    //@Override
    public void execute(Tran meta, RunnableEx runnable) throws Throwable {
        if (meta == null) {
            //
            //如果没有注解或工厂，直接运行
            //
            runnable.run();
            return;
        }

        switch (meta.policy()) {
            case supports: {
                runnable.run();
                return;
            }
            case not_supported: {
                tranNot.apply(runnable);
                return;
            }
            case never: {
                tranNever.apply(runnable);
                return;
            }
            case mandatory: {
                tranMandatory.apply(runnable);
                return;
            }
        }

        Stack<TranEntity> stack = local.get();

        //根事务不存在
        if (stack == null) {
            forRoot(stack, meta, runnable);
        } else {
            forNotRoot(stack, meta, runnable);
        }
    }

    /**
     * 执行根节点的事务
     */
    protected void forRoot(Stack<TranEntity> stack, Tran meta, RunnableEx runnable) throws Throwable {
        //::必须 或新建 或嵌套  //::入栈
        //
        TranNode tran = create(meta);
        stack = new Stack<>();

        try {
            local.set(stack);
            applyDo(stack, tran, meta, runnable);
        } finally {
            local.remove();
        }
    }

    /**
     * 执行非根节点的事务
     */
    protected void forNotRoot(Stack<TranEntity> stack, Tran meta, RunnableEx runnable) throws Throwable {
        switch (meta.policy()) {
            case required: {
                //::支持当前事务
                runnable.run();
                return;
            }

            case requires_new: {
                //::新起一个独立事务    //::入栈
                TranNode tran = create(meta);
                applyDo(stack, tran, meta, runnable);
                return;
            }

            case nested: {
                //::在当前事务内部嵌套一个事务 //::入栈
                TranNode tran = create(meta);

                //::加入上个事务***
                stack.peek().tran.add(tran);

                applyDo(stack, tran, meta, runnable);
                return;
            }
        }
    }


    protected void applyDo(Stack<TranEntity> stack, TranNode tran, Tran meta, RunnableEx runnable) throws Throwable {
        if (meta.policy().code <= TranPolicy.nested.code) {
            //required || requires_new || nested ，需要入栈
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

    /**
     * 创建一个事务节点
     */
    protected TranNode create(Tran meta) {
        if (meta.policy() == TranPolicy.not_supported) {
            //事务排除
            return tranNot;
        } else if (meta.policy() == TranPolicy.never) {
            //决不能有事务
            return tranNever;
        } else if (meta.policy() == TranPolicy.mandatory) {
            //必须要有当前事务
            return tranMandatory;
        } else {
            //事务 required || (requires_new || nested)
            //
            if (meta.policy() == TranPolicy.requires_new
                    || meta.policy() == TranPolicy.nested) {
                return new TranDbNewImp(meta);
            } else {
                return new TranDbImp(meta);
            }

        }
    }
}
