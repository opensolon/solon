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
import io.github.kiryu1223.expressionTree.delegate.Action6;
import io.github.kiryu1223.expressionTree.delegate.Func6;
import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate6<T1, T2, T3, T4, T5, T6> extends UpdateBase
{
    public LUpdate6(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LUpdate7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(Class<Tn> target, @Expr Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LUpdate7<>(getSqlBuilder());
    }

    public <Tn> LUpdate7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(Class<Tn> target, @Expr Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LUpdate7<>(getSqlBuilder());
    }

    public <Tn> LUpdate7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(Class<Tn> target, @Expr Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate7<>(getSqlBuilder());
    }
    //endregion

    //region [SET]
    public LUpdate6<T1, T2, T3, T4, T5, T6> set(@Expr Action6<T1, T2, T3, T4, T5, T6> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate6<T1, T2, T3, T4, T5, T6> set(ExprTree<Action6<T1, T2, T3, T4, T5, T6>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate6<T1, T2, T3, T4, T5, T6> where(@Expr Func6<T1, T2, T3, T4, T5, T6, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate6<T1, T2, T3, T4, T5, T6> where(ExprTree<Func6<T1, T2, T3, T4, T5, T6, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
