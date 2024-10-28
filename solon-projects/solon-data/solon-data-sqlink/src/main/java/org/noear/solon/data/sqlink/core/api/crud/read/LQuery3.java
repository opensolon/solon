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

import io.github.kiryu1223.expressionTree.delegate.Func3;
import io.github.kiryu1223.expressionTree.delegate.Func4;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.api.Result;
import org.noear.solon.data.sqlink.core.api.crud.read.group.GroupedQuery3;
import org.noear.solon.data.sqlink.core.api.crud.read.group.Grouper;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class LQuery3<T1, T2, T3> extends QueryBase
{
    // region [INIT]

    public LQuery3(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    @Override
    protected <Tn> LQuery4<T1, T2, T3, Tn> joinNewQuery()
    {
        return new LQuery4<>(getSqlBuilder());
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> innerJoin(Class<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> leftJoin(Class<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> rightJoin(Class<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery4<T1, T2, T3, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    // endregion

    // region [WHERE]
    public LQuery3<T1, T2, T3> where(@Expr(Expr.BodyType.Expr) Func3<T1, T2, T3, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery3<T1, T2, T3> where(ExprTree<Func3<T1, T2, T3, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public LQuery3<T1, T2, T3> orWhere(@Expr(Expr.BodyType.Expr) Func3<T1, T2, T3, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery3<T1, T2, T3> orWhere(ExprTree<Func3<T1, T2, T3, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> LQuery3<T1, T2, T3> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery3<T1, T2, T3> exists(Class<E> table, ExprTree<Func4<T1, T2, T3, E, Boolean>> expr)
    {
        exists(table, expr.getTree(), false);
        return this;
    }

    public <E> LQuery3<T1, T2, T3> exists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery3<T1, T2, T3> exists(LQuery<E> query, ExprTree<Func4<T1, T2, T3, E, Boolean>> expr)
    {
        exists(query, expr.getTree(), false);
        return this;
    }

    public <E> LQuery3<T1, T2, T3> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery3<T1, T2, T3> notExists(Class<E> table, ExprTree<Func4<T1, T2, T3, E, Boolean>> expr)
    {
        exists(table, expr.getTree(), true);
        return this;
    }

    public <E> LQuery3<T1, T2, T3> notExists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func4<T1, T2, T3, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery3<T1, T2, T3> notExists(LQuery<E> query, ExprTree<Func4<T1, T2, T3, E, Boolean>> expr)
    {
        exists(query, expr.getTree(), true);
        return this;
    }
    // endregion

    // region [ORDER BY]
    public <R> LQuery3<T1, T2, T3> orderBy(@Expr(Expr.BodyType.Expr) Func3<T1, T2, T3, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery3<T1, T2, T3> orderBy(ExprTree<Func3<T1, T2, T3, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery3<T1, T2, T3> orderBy(@Expr(Expr.BodyType.Expr) Func3<T1, T2, T3, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery3<T1, T2, T3> orderBy(ExprTree<Func3<T1, T2, T3, R>> expr)
    {
        orderBy(expr, true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public LQuery3<T1, T2, T3> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public LQuery3<T1, T2, T3> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [GROUP BY]
    public <Key extends Grouper> GroupedQuery3<Key, T1, T2, T3> groupBy(@Expr Func3<T1, T2, T3, Key> expr)
    {
        throw new NotCompiledException();
    }

    public <Key extends Grouper> GroupedQuery3<Key, T1, T2, T3> groupBy(ExprTree<Func3<T1, T2, T3, Key>> expr)
    {
        groupBy(expr.getTree());
        return new GroupedQuery3<>(getSqlBuilder());
    }
    // endregion

    // region [SELECT]
    public <R> EndQuery<R> select(@Recode Class<R> r)
    {
        return super.select(r);
    }

    public <R extends Result> LQuery<R> select(@Expr Func3<T1, T2, T3, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<R> select(ExprTree<Func3<T1, T2, T3, R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func3<T1, T2, T3, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func3<T1, T2, T3, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
    // endregion

    //region [OTHER]

    public LQuery3<T1, T2, T3> distinct()
    {
        distinct0(true);
        return this;
    }

    public LQuery3<T1, T2, T3> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }

    // endregion
}
