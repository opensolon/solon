/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.data.sqlink.api.crud.update;

import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class LUpdate7<T1, T2, T3, T4, T5, T6, T7> extends UpdateBase {
    public LUpdate7(UpdateSqlBuilder sqlBuilder) {
        super(sqlBuilder);
    }

    //region [JOIN]

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的更新过程对象
     */
    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr) {
        join(JoinType.INNER, target, expr);
        return new LUpdate8<>(getSqlBuilder());
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的更新过程对象
     */
    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr) {
        join(JoinType.LEFT, target, expr);
        return new LUpdate8<>(getSqlBuilder());
    }

    /**
     * join表操作<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param target 数据表类
     * @param func   返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param <Tn>   join过来的表的类型
     * @return 泛型数量+1的更新过程对象
     */
    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func) {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr) {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate8<>(getSqlBuilder());
    }
    //endregion

    //region [SET]

    /**
     * 选择需要更新的字段和值
     * <p><b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 需要更新的字段(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> set(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> func, R value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> set(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> func, R value) {
        set(func.getTree(), value);
        return this;
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> setInDb(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> func, Func7<T1, T2, T3, T4, T5, T6, T7, R> value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> setInDb(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> func, ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> value) {
        set(func.getTree(), value.getTree());
        return this;
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> setIf(boolean condition, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> func, R value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> setIf(boolean condition, ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> func, R value) {
        if (condition) set(func.getTree(), value);
        return this;
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> setInDbIf(boolean condition, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> func, Func7<T1, T2, T3, T4, T5, T6, T7, R> value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate7<T1, T2, T3, T4, T5, T6, T7> setInDbIf(boolean condition, ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> func, ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> value) {
        if (condition) set(func.getTree(), value.getTree());
        return this;
    }

    //endregion

    //region [WHERE]

    /**
     * 设置where条件<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> where(@Expr Func7<T1, T2, T3, T4, T5, T6, T7, Boolean> func) {
        throw new NotCompiledException();
    }

    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> where(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Boolean>> expr) {
        where(expr.getTree());
        return this;
    }

    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> whereIf(boolean condition, @Expr Func7<T1, T2, T3, T4, T5, T6, T7, Boolean> func) {
        throw new NotCompiledException();
    }

    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> whereIf(boolean condition, ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Boolean>> expr) {
        if (condition) where(expr.getTree());
        return this;
    }

    //endregion
}
