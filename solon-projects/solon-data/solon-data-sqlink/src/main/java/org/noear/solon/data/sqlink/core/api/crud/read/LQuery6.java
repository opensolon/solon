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

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.api.crud.read.group.GroupedQuery6;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func6;
import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;

public class LQuery6<T1, T2, T3, T4, T5, T6> extends QueryBase
{
    // region [INIT]

    public LQuery6(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    @Override
    protected <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> joinNewQuery()
    {
        return new LQuery7<>(getSqlBuilder());
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    // endregion

    // region [WHERE]
    public LQuery6<T1, T2, T3, T4, T5, T6> where(@Expr(Expr.BodyType.Expr) Func6<T1, T2, T3, T4, T5, T6, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery6<T1, T2, T3, T4, T5, T6> where(ExprTree<Func6<T1, T2, T3, T4, T5, T6, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public LQuery6<T1, T2, T3, T4, T5, T6> orWhere(@Expr(Expr.BodyType.Expr) Func6<T1, T2, T3, T4, T5, T6, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery6<T1, T2, T3, T4, T5, T6> orWhere(ExprTree<Func6<T1, T2, T3, T4, T5, T6, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> exists(Class<E> table, ExprTree<Func7<T1, T2, T3, T4, T5, T6, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),false);
        return this;
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> exists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> exists(LQuery<E> query, ExprTree<Func7<T1, T2, T3, T4, T5, T6, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),false);
        return this;
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> notExists(Class<E> table, ExprTree<Func7<T1, T2, T3, T4, T5, T6, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),true);
        return this;
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> notExists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery6<T1, T2, T3, T4, T5, T6> notExists(LQuery<E> query, ExprTree<Func7<T1, T2, T3, T4, T5, T6, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),true);
        return this;
    }
    // endregion

    // region [ORDER BY]
    public <R> LQuery6<T1, T2, T3, T4, T5, T6> orderBy(@Expr(Expr.BodyType.Expr) Func6<T1, T2, T3, T4, T5, T6, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery6<T1, T2, T3, T4, T5, T6> orderBy(ExprTree<Func6<T1, T2, T3, T4, T5, T6, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery6<T1, T2, T3, T4, T5, T6> orderBy(@Expr(Expr.BodyType.Expr) Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery6<T1, T2, T3, T4, T5, T6> orderBy(ExprTree<Func6<T1, T2, T3, T4, T5, T6, R>> expr)
    {
        orderBy(expr, true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public LQuery6<T1, T2, T3, T4, T5, T6> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public LQuery6<T1, T2, T3, T4, T5, T6> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [GROUP BY]
    public <Key> GroupedQuery6<Key, T1, T2, T3, T4, T5, T6> groupBy(@Expr Func6<T1, T2, T3, T4, T5, T6, Key> expr)
    {
        throw new NotCompiledException();
    }

    public <Key> GroupedQuery6<Key, T1, T2, T3, T4, T5, T6> groupBy(ExprTree<Func6<T1, T2, T3, T4, T5, T6, Key>> expr)
    {
        groupBy(expr.getTree());
        return new GroupedQuery6<>(getSqlBuilder());
    }
    // endregion

    // region [SELECT]
    public <R> EndQuery<R> select(@Recode Class<R> r)
    {
        return super.select(r);
    }

    public <R> LQuery<? extends R> select(@Expr Func6<T1, T2, T3, T4, T5, T6, ? extends R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<? extends R> select(ExprTree<Func6<T1, T2, T3, T4, T5, T6, ? extends R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func6<T1, T2, T3, T4, T5, T6, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
    // endregion

    //region [OTHER]

    public LQuery6<T1, T2, T3, T4, T5, T6> distinct()
    {
        distinct0(true);
        return this;
    }

    public LQuery6<T1, T2, T3, T4, T5, T6> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }

    //endregion

    // region [toAny]

    // endregion
}
