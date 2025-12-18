/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.tran;

import org.noear.solon.util.ScopeLocal;
import org.noear.solon.data.annotation.Transaction;
import org.noear.solon.data.tran.impl.*;
import org.noear.solon.core.util.RunnableEx;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

/**
 * 事务执行器实现
 *
 * 基于 节点 与 栈管理
 *
 * @author noear
 * @since 1.0
 * */
public class TranExecutorDefault implements TranExecutor {
    public static final TranExecutorDefault global = new TranExecutorDefault();

    protected TranExecutorDefault() {

    }

    protected final ScopeLocal<Stack<TranEntity>> LOCAL = ScopeLocal.newInstance(TranExecutorDefault.class);

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

    @Override
    public void listen(TranListener listener) throws IllegalStateException {
        if (listener == null) {
            return;
        }

        DbTran tran = TranManager.current();
        if (tran == null) {
            throw new IllegalStateException("The current tran is not active");
        }

        tran.listen(listener);
    }

    protected TranNode tranNot = new TranNotImpl();
    protected TranNode tranNever = new TranNeverImpl();
    protected TranNode tranMandatory = new TranMandatoryImpl();

    /**
     * 执行事务
     *
     * @param meta     事务注解
     * @param runnable 真实执行器
     *
     */
    @Override
    public void execute(Transaction meta, RunnableEx runnable) throws Throwable {
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

        Stack<TranEntity> stack = LOCAL.get();

        //根事务不存在
        if (stack == null) {
            forRoot(meta, runnable);
        } else {
            forNotRoot(stack, meta, runnable);
        }
    }

    /**
     * 执行根节点的事务
     *
     * @param meta     事务注解
     * @param runnable 真实执行器
     */
    protected void forRoot(Transaction meta, RunnableEx runnable) throws Throwable {
        //::必须 或新建 或嵌套  //::入栈
        //
        TranNode tran = create(meta);

        LOCAL.withOrThrow(new Stack<>(), () -> {
            applyDo(LOCAL.get(), tran, meta, runnable);
        });
    }

    /**
     * 执行非根节点的事务
     *
     * @param stack    事务栈
     * @param meta     事务注解
     * @param runnable 真实执行器
     */
    protected void forNotRoot(Stack<TranEntity> stack, Transaction meta, RunnableEx runnable) throws Throwable {
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
                if (stack != null && !stack.isEmpty()) {
                    stack.peek().tran.add(tran);
                }

                applyDo(stack, tran, meta, runnable);
                return;
            }
        }
    }


    /**
     * 应用事务
     *
     * @param stack    事务栈
     * @param tran     事务节点
     * @param meta     事务注解
     * @param runnable 真实执行器
     *
     */
    protected void applyDo(Stack<TranEntity> stack, TranNode tran, Transaction meta, RunnableEx runnable) throws Throwable {
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
     *
     * @param meta 事务注解
     */
    protected TranNode create(Transaction meta) {
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
                return new TranDbNewImpl(meta);
            } else {
                return new TranDbImpl(meta);
            }

        }
    }
}