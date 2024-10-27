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
package org.noear.solon.data.sqlink.core.api.crud.update;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action4;
import io.github.kiryu1223.expressionTree.delegate.Func4;
import io.github.kiryu1223.expressionTree.delegate.Func5;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class LUpdate4<T1, T2, T3, T4> extends UpdateBase
{
    public LUpdate4(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LUpdate5<T1, T2, T3, T4, Tn> innerJoin(Class<Tn> target, @Expr Func5<T1, T2, T3, T4, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate5<T1, T2, T3, T4, Tn> innerJoin(Class<Tn> target, ExprTree<Func5<T1, T2, T3, T4, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LUpdate5<>(getSqlBuilder());
    }

    public <Tn> LUpdate5<T1, T2, T3, T4, Tn> leftJoin(Class<Tn> target, @Expr Func5<T1, T2, T3, T4, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate5<T1, T2, T3, T4, Tn> leftJoin(Class<Tn> target, ExprTree<Func5<T1, T2, T3, T4, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LUpdate5<>(getSqlBuilder());
    }

    public <Tn> LUpdate5<T1, T2, T3, T4, Tn> rightJoin(Class<Tn> target, @Expr Func5<T1, T2, T3, T4, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate5<T1, T2, T3, T4, Tn> rightJoin(Class<Tn> target, ExprTree<Func5<T1, T2, T3, T4, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate5<>(getSqlBuilder());
    }
    //endregion

    //region [SET]
    public LUpdate4<T1, T2, T3, T4> set(@Expr Action4<T1, T2, T3, T4> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate4<T1, T2, T3, T4> set(ExprTree<Action4<T1, T2, T3, T4>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate4<T1, T2, T3, T4> where(@Expr Func4<T1, T2, T3, T4, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate4<T1, T2, T3, T4> where(ExprTree<Func4<T1, T2, T3, T4, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
