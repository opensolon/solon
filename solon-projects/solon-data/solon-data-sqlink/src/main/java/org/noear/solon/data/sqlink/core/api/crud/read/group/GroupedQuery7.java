package org.noear.solon.data.sqlink.core.api.crud.read.group;


import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.api.crud.read.EndQuery;
import org.noear.solon.data.sqlink.core.api.crud.read.LQuery;
import org.noear.solon.data.sqlink.core.api.crud.read.QueryBase;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

import java.util.List;

public class GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> extends QueryBase
{
    public GroupedQuery7(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // region [HAVING]
    public GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> having(@Expr(Expr.BodyType.Expr) Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> having(ExprTree<Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, Boolean>> expr)
    {
        having(expr.getTree());
        return this;
    }

    // endregion

    // region [ORDER BY]
    public <R> GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> orderBy(@Expr(Expr.BodyType.Expr) Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> orderBy(ExprTree<Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> orderBy(@Expr(Expr.BodyType.Expr) Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> orderBy(ExprTree<Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, R>> expr)
    {
        orderBy(expr, true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [SELECT]
    public <R> LQuery<? extends R> select(@Expr Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, ? extends R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<? extends R> select(ExprTree<Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, ? extends R>> expr)
    {
        singleCheck(select(expr.getTree()));
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func1<Group7<Key, T1, T2, T3, T4, T5, T6, T7>, R>> expr)
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
    public List<Key> toList()
    {
        return super.toList();
    }

    public GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> distinct()
    {
        distinct0(true);
        return this;
    }

    public GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }
}
