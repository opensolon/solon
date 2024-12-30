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

import io.github.kiryu1223.expressionTree.delegate.Func10;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends UpdateBase {
    public LUpdate10(UpdateSqlBuilder sqlBuilder) {
        super(sqlBuilder);
    }

    //region [SET]

    /**
     * 选择需要更新的字段和值
     * <p><b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 需要更新的字段(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(@Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> func, R value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> func, R value) {
        set(func.getTree(), value);
        return this;
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> setInDb(@Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> func, Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> setInDb(ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> func, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> value) {
        set(func.getTree(), value.getTree());
        return this;
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> setIf(boolean condition, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> func, R value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> setIf(boolean condition, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> func, R value) {
        if (condition) set(func.getTree(), value);
        return this;
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> setInDbIf(boolean condition, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> func, Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> value) {
        throw new NotCompiledException();
    }

    public <R> LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> setInDbIf(boolean condition, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> func, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R>> value) {
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
    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> where(@Expr Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean> func) {
        throw new NotCompiledException();
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> where(ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean>> expr) {
        where(expr.getTree());
        return this;
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> whereIf(boolean condition, @Expr Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean> func) {
        throw new NotCompiledException();
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> whereIf(boolean condition, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean>> expr) {
        if (condition) where(expr.getTree());
        return this;
    }

    //endregion
}
