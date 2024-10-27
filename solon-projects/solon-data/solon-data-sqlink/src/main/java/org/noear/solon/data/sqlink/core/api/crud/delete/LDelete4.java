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
package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func4;
import io.github.kiryu1223.expressionTree.delegate.Func5;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class LDelete4<T1,T2,T3,T4> extends DeleteBase
{
    public LDelete4(DeleteSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LDelete5<T1, T2, T3, T4, Tn> innerJoin(Class<Tn> target, @Expr Func5<T1, T2, T3, T4, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete5<T1, T2, T3, T4, Tn> innerJoin(Class<Tn> target, ExprTree<Func5<T1, T2, T3, T4, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LDelete5<>(getSqlBuilder());
    }

    public <Tn> LDelete5<T1, T2, T3, T4, Tn> leftJoin(Class<Tn> target, @Expr Func5<T1, T2, T3, T4, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete5<T1, T2, T3, T4, Tn> leftJoin(Class<Tn> target, ExprTree<Func5<T1, T2, T3, T4, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LDelete5<>(getSqlBuilder());
    }

    public <Tn> LDelete5<T1, T2, T3, T4, Tn> rightJoin(Class<Tn> target, @Expr Func5<T1, T2, T3, T4, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete5<T1, T2, T3, T4, Tn> rightJoin(Class<Tn> target, ExprTree<Func5<T1, T2, T3, T4, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LDelete5<>(getSqlBuilder());
    }
    //endregion

    // region [WHERE]
    public LDelete4<T1,T2,T3,T4> where(@Expr Func4<T1,T2,T3,T4, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LDelete4<T1,T2,T3,T4> where(ExprTree<Func4<T1,T2,T3,T4, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    // endregion

    public <R> LDelete4<T1,T2,T3,T4> selectDelete(@Expr(Expr.BodyType.Expr) Func4<T1,T2,T3,T4, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LDelete4<T1,T2,T3,T4> selectDelete(ExprTree<Func4<T1,T2,T3,T4, R>> expr)
    {
        Class<?> returnType = expr.getTree().getReturnType();
        selectDeleteTable(returnType);
        return this;
    }
}
