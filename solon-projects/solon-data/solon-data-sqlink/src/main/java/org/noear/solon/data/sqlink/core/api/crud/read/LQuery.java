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

import org.noear.solon.data.sqlink.core.api.crud.read.group.GroupedQuery;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action1;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;

import java.util.*;


public class LQuery<T> extends QueryBase
{
    // region [INIT]

    public LQuery(IConfig config, Class<T> c)
    {
        super(config, c);
    }

    public LQuery(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    protected <Tn> LQuery2<T, Tn> joinNewQuery()
    {
        return new LQuery2<>(getSqlBuilder());
    }

    public <Tn> LQuery2<T, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> innerJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(EndQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> leftJoin(EndQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> rightJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery2<T, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery2<T, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    //endregion

    // region [WHERE]

    public LQuery<T> where(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery<T> where(ExprTree<Func1<T, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public LQuery<T> orWhere(@Expr(Expr.BodyType.Expr) Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery<T> orWhere(ExprTree<Func1<T, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> LQuery<T> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery<T> exists(Class<E> table, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(table, expr.getTree(), false);
        return this;
    }

    public <E> LQuery<T> exists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery<T> exists(LQuery<E> query, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(query, expr.getTree(), false);
        return this;
    }

    public <E> LQuery<T> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery<T> notExists(Class<E> table, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(table, expr.getTree(), true);
        return this;
    }

    public <E> LQuery<T> notExists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func2<T, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery<T> notExists(LQuery<E> query, ExprTree<Func2<T, E, Boolean>> expr)
    {
        exists(query, expr.getTree(), true);
        return this;
    }

    // endregion

    // region [ORDER BY]

    public <R> LQuery<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderBy(ExprTree<Func1<T, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery<T> orderBy(@Expr(Expr.BodyType.Expr) Func1<T, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<T> orderBy(ExprTree<Func1<T, R>> expr)
    {
        orderBy(expr, true);
        return this;
    }

    // endregion

    // region [LIMIT]

    public LQuery<T> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public LQuery<T> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }

    // endregion

    // region [GROUP BY]

    public <Key> GroupedQuery<Key, T> groupBy(@Expr Func1<T, Key> expr)
    {
        throw new NotCompiledException();
    }

    public <Key> GroupedQuery<Key, T> groupBy(ExprTree<Func1<T, Key>> expr)
    {
        groupBy(expr.getTree());
        return new GroupedQuery<>(getSqlBuilder());
    }

    // endregion

    // region [SELECT]

    public <R> EndQuery<R> select(@Recode Class<R> r)
    {
        return super.select(r);
    }

    public LQuery<T> select()
    {
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> LQuery<? extends R> select(@Expr Func1<T, ? extends R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<? extends R> select(ExprTree<Func1<T, ? extends R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func1<T, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func1<T, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }

    // endregion

    // region [INCLUDE]

    public <R> IncludeQuery<T, R> include(@Expr(Expr.BodyType.Expr) Func1<T, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeQuery<T, R> include(ExprTree<Func1<T, R>> expr)
    {
        include(expr.getTree());
        return new IncludeQuery<>(getSqlBuilder());
    }

    public <R> IncludeQuery<T, R> include(@Expr(Expr.BodyType.Expr) Func1<T, R> expr, @Expr(Expr.BodyType.Expr) Func1<R, Boolean> cond)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeQuery<T, R> include(ExprTree<Func1<T, R>> expr, ExprTree<Func1<R, Boolean>> cond)
    {
        include(expr.getTree(), cond.getTree());
        return new IncludeQuery<>(getSqlBuilder());
    }

    public <R> IncludeQuery<T, R> includes(@Expr(Expr.BodyType.Expr) Func1<T, Collection<R>> expr)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeQuery<T, R> includes(ExprTree<Func1<T, Collection<R>>> expr)
    {
        include(expr.getTree());
        return new IncludeQuery<>(getSqlBuilder());
    }

    public <R> IncludeQuery<T, R> includes(@Expr(Expr.BodyType.Expr) Func1<T, Collection<R>> expr, @Expr(Expr.BodyType.Expr) Func1<R, Boolean> cond)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeQuery<T, R> includes(ExprTree<Func1<T, Collection<R>>> expr, ExprTree<Func1<R, Boolean>> cond)
    {
        include(expr.getTree(), cond.getTree());
        return new IncludeQuery<>(getSqlBuilder());
    }

    public <R> IncludeQuery<T, R> includesByCond(@Expr(Expr.BodyType.Expr) Func1<T, Collection<R>> expr, Action1<IncludeCond<R>> cond)
    {
        throw new NotCompiledException();
    }

    public <R> IncludeQuery<T, R> includesByCond(ExprTree<Func1<T, Collection<R>>> expr, Action1<IncludeCond<R>> action)
    {
        includeByCond(expr.getTree(), action);
        return new IncludeQuery<>(getSqlBuilder());
    }

    // endregion

    //region [OTHER]

    public LQuery<T> distinct()
    {
        distinct0(true);
        return this;
    }

    public LQuery<T> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }

    //endregion

    // region [toAny]

    @Override
    public boolean any()
    {
        return super.any();
    }

    @Override
    public T first()
    {
        return super.first();
    }

    public List<T> toList()
    {
        return super.toList();
    }

    public <R> List<R> toList(Func1<T, R> func)
    {
        List<R> rList = new ArrayList<>();
        for (T t : toList())
        {
            rList.add(func.invoke(t));
        }
        return rList;
    }

    public <K> Map<K, T> toMap(Func1<T, K> func)
    {
        return toMap(func, new HashMap<>());
    }

    public <K> Map<K, T> toMap(Func1<T, K> func, Map<K, T> map)
    {
        for (T t : toList())
        {
            map.put(func.invoke(t), t);
        }
        return map;
    }

    // endregion
}
