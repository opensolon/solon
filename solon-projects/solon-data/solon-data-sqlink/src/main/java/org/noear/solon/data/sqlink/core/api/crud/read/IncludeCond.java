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
package org.noear.solon.data.sqlink.core.api.crud.read;

import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class IncludeCond<T> extends QueryBase
{
    public IncludeCond(IConfig config, Class<?> c)
    {
        super(config, c);
    }

    // region [WHERE]

    /**
     * 设置where条件<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public IncludeCond<T> where(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public IncludeCond<T> where(ExprTree<Func1<T, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    /**
     * 设置where条件，并且以or将多个where连接<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param func 返回bool的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public IncludeCond<T> orWhere(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public IncludeCond<T> orWhere(ExprTree<Func1<T, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    // endregion

    // region [ORDER BY]

    /**
     * 设置orderBy的字段以及升降序，多次调用可以指定多个orderBy字段<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @param asc  是否为升序
     * @return this
     */
    public <R> IncludeCond<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeCond<T> orderBy(ExprTree<Func1<T, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    /**
     * 设置orderBy的字段并且为升序，多次调用可以指定多个orderBy字段<p>
     * <b>注意：此函数的ExprTree[func类型]版本为真正被调用的函数
     *
     * @param expr 返回需要的字段的lambda表达式(强制要求参数为<b>lambda表达式</b>，不可以是<span style='color:red;'>方法引用</span>以及<span style='color:red;'>匿名对象</span>)
     * @return this
     */
    public <R> IncludeCond<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeCond<T> orderBy(ExprTree<Func1<T, R>> expr)
    {
        orderBy(expr, true);
        return this;
    }

    // endregion

    // region [LIMIT]

    /**
     * 获取指定数量的数据
     *
     * @param rows 需要返回的条数
     * @return this
     */
    public IncludeCond<T> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    /**
     * 跳过指定数量条数据，再指定获取指定数量的数据
     *
     * @param offset 需要跳过的条数
     * @param rows   需要返回的条数
     * @return this
     */
    public IncludeCond<T> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }

    // endregion
}
