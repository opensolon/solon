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
package org.noear.solon.data.sqlink.core.api.crud.read.group;

import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import org.noear.solon.data.sqlink.core.api.Result;
import org.noear.solon.data.sqlink.core.api.crud.read.EndQuery;
import org.noear.solon.data.sqlink.core.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.core.api.crud.read.QueryBase;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class GroupedQuery2<Key, T1, T2> extends QueryBase
{
    public GroupedQuery2(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // region [HAVING]
    public GroupedQuery2<Key, T1, T2> having(@Expr(Expr.BodyType.Expr) Func1<Group2<Key, T1, T2>, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public GroupedQuery2<Key, T1, T2> having(ExprTree<Func1<Group2<Key, T1, T2>, Boolean>> expr)
    {
        having(expr.getTree());
        return this;
    }
    // endregion

    // region [ORDER BY]
    public <R> GroupedQuery2<Key, T1, T2> orderBy(@Expr(Expr.BodyType.Expr) Func1<Group2<Key, T1, T2>, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery2<Key, T1, T2> orderBy(ExprTree<Func1<Group2<Key, T1, T2>, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> GroupedQuery2<Key, T1, T2> orderBy(@Expr(Expr.BodyType.Expr) Func1<Group2<Key, T1, T2>, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery2<Key, T1, T2> orderBy(ExprTree<Func1<Group2<Key, T1, T2>, R>> expr)
    {
        orderBy(expr, true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public GroupedQuery2<Key, T1, T2> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public GroupedQuery2<Key, T1, T2> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [SELECT]
    public <R extends Result> LQuery<R> select(@Expr Func1<Group2<Key, T1, T2>, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R extends Result> LQuery<R> select(ExprTree<Func1<Group2<Key, T1, T2>, R>> expr)
    {
        singleCheck(select(expr.getTree()));
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func1<Group2<Key, T1, T2>, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func1<Group2<Key, T1, T2>, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(boxedQuerySqlBuilder());
    }
    // endregion

    @Override
    public boolean any()
    {
        return super.any();
    }

    @Override
    public List<? extends Key> toList()
    {
        return super.toList();
    }

    public GroupedQuery2<Key, T1, T2> distinct()
    {
        distinct0(true);
        return this;
    }

    public GroupedQuery2<Key, T1, T2> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }
}
