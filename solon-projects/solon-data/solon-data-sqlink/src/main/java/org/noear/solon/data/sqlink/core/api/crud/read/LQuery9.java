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
import org.noear.solon.data.sqlink.core.api.Result;
import org.noear.solon.data.sqlink.core.api.crud.read.group.Grouper;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.api.crud.read.group.GroupedQuery9;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func10;
import io.github.kiryu1223.expressionTree.delegate.Func9;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends QueryBase
{
    // region [INIT]

    public LQuery9(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    @Override
    protected <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> joinNewQuery()
    {
        return new LQuery10<>(getSqlBuilder());
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> innerJoin(Class<Tn> target, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> leftJoin(Class<Tn> target, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> rightJoin(Class<Tn> target, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    // endregion

    // region [WHERE]
    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> where(@Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> where(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> orWhere(@Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> orWhere(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> exists(Class<E> table, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),false);
        return this;
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> exists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> exists(LQuery<E> query, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),false);
        return this;
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notExists(Class<E> table, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),true);
        return this;
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notExists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> notExists(LQuery<E> query, ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),true);
        return this;
    }
    // endregion

    // region [ORDER BY]
    public <R> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(@Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(@Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> orderBy(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> expr)
    {
        orderBy(expr.getTree(), true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [GROUP BY]
    public <Key extends Grouper> GroupedQuery9<Key, T1, T2, T3, T4, T5, T6, T7, T8, T9> groupBy(@Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Key> expr)
    {
        throw new NotCompiledException();
    }

    public <Key extends Grouper> GroupedQuery9<Key, T1, T2, T3, T4, T5, T6, T7, T8, T9> groupBy(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Key>> expr)
    {
        groupBy(expr.getTree());
        return new GroupedQuery9<>(getSqlBuilder());
    }
    // endregion

    // region [SELECT]
    public <R> EndQuery<R> select(@Recode Class<R> r)
    {
        return super.select(r);
    }

    public <R extends Result> LQuery<R> select(@Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<R> select(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9,R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }

    // endregion

    //region [OTHER]

    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> distinct()
    {
        distinct0(true);
        return this;
    }

    public LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, T9> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }

    //endregion

}
