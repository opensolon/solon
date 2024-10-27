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

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;

public class IncludeCond<T> extends QueryBase
{
    public IncludeCond(IConfig config, Class<?> c)
    {
        super(config, c);
    }

    // region [WHERE]

    public IncludeCond<T> where(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public IncludeCond<T> where(ExprTree<Func1<T, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public IncludeCond<T> orWhere(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public IncludeCond<T> orWhere(ExprTree<Func1<T, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> IncludeCond<T> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> IncludeCond<T> exists(Class<E> table, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(table, expr.getTree(), false);
        return this;
    }

    public <E> IncludeCond<T> exists(IncludeCond<E> query, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> IncludeCond<T> exists(IncludeCond<E> query, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(query, expr.getTree(), false);
        return this;
    }

    public <E> IncludeCond<T> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> IncludeCond<T> notExists(Class<E> table, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(table, expr.getTree(), true);
        return this;
    }

    public <E> IncludeCond<T> notExists(IncludeCond<E> query, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> IncludeCond<T> notExists(IncludeCond<E> query, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(query, expr.getTree(), true);
        return this;
    }

    // endregion

    // region [ORDER BY]

    public <R> IncludeCond<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeCond<T> orderBy(ExprTree<Func1<T, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

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

    public IncludeCond<T> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public IncludeCond<T> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }

    // endregion
}
