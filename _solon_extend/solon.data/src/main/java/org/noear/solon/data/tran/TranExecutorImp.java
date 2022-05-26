package org.noear.solon.data.tran;

import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tranImp.*;
import org.noear.solon.ext.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

/**
 * 事务执行器实现
 *
 * 基于 节点 与 栈管理
 * */
public class TranExecutorImp implements TranExecutor {
    public static final TranExecutorImp global = new TranExecutorImp();

    protected TranExecutorImp() {

    }

    protected ThreadLocal<Stack<TranEntity>> local = new ThreadLocal<>();

    /**
     * 是否在事务中
     */
    @Override
    public boolean inTrans() {
        return TranManager.current() != null;
    }

    /**
     * 是否在事务中且只读
     */
    @Override
    public boolean inTransAndReadOnly() {
        DbTran tran = TranManager.current();
        return tran != null && tran.getMeta().readOnly();
    }

    /**
     * 获取链接
     *
     * @param ds 数据源
     */
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

    /**
     * 执行事务
     *
     * @param meta 事务注解
     * @param executor 真实执行器
     * */
    public void execute(Tran meta, RunnableEx executor) throws Throwable {
        if (meta == null) {
            //
            //如果没有注解或工厂，直接运行
            //
            executor.run();
            return;
        }

        switch (meta.policy()) {
            case supports: {
                executor.run();
                return;
            }
            case not_supported: {
                tranNot.apply(executor);
                return;
            }
            case never: {
                tranNever.apply(executor);
                return;
            }
            case mandatory: {
                tranMandatory.apply(executor);
                return;
            }
        }

        Stack<TranEntity> stack = local.get();

        //根事务不存在
        if (stack == null) {
            forRoot(stack, meta, executor);
        } else {
            forNotRoot(stack, meta, executor);
        }
    }

    /**
     * 执行根节点的事务
     *
     * @param stack 事务栈
     * @param meta 事务注解
     * @param executor 真实执行器
     */
    protected void forRoot(Stack<TranEntity> stack, Tran meta, RunnableEx executor) throws Throwable {
        //::必须 或新建 或嵌套  //::入栈
        //
        TranNode tran = create(meta);
        stack = new Stack<>();

        try {
            local.set(stack);
            applyDo(stack, tran, meta, executor);
        } finally {
            local.remove();
        }
    }

    /**
     * 执行非根节点的事务
     *
     * @param stack 事务栈
     * @param meta 事务注解
     * @param executor 真实执行器
     */
    protected void forNotRoot(Stack<TranEntity> stack, Tran meta, RunnableEx executor) throws Throwable {
        switch (meta.policy()) {
            case required: {
                //::支持当前事务
                executor.run();
                return;
            }

            case requires_new: {
                //::新起一个独立事务    //::入栈
                TranNode tran = create(meta);
                applyDo(stack, tran, meta, executor);
                return;
            }

            case nested: {
                //::在当前事务内部嵌套一个事务 //::入栈
                TranNode tran = create(meta);

                //::加入上个事务***
                stack.peek().tran.add(tran);

                applyDo(stack, tran, meta, executor);
                return;
            }
        }
    }


    /**
     * 应用事务
     *
     * @param stack 事务栈
     * @param tran 事务节点
     * @param meta 事务注解
     * @param executor 真实执行器
     * */
    protected void applyDo(Stack<TranEntity> stack, TranNode tran, Tran meta, RunnableEx executor) throws Throwable {
        if (meta.policy().code <= TranPolicy.nested.code) {
            //required || requires_new || nested ，需要入栈
            //
            try {
                //入栈
                stack.push(new TranEntity(tran, meta));
                tran.apply(executor);
            } finally {
                //出栈
                stack.pop();
            }
        } else {
            //不需要入栈
            //
            tran.apply(executor);
        }
    }

    /**
     * 创建一个事务节点
     *
     * @param meta 事务注解
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
