package org.noear.solon.data.sqlink.core.api.crud.read;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.api.crud.read.group.GroupedQuery7;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;

public class LQuery7<T1, T2, T3, T4, T5, T6, T7> extends QueryBase
{
    // region [INIT]

    public LQuery7(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    @Override
    protected <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> joinNewQuery()
    {
        return new LQuery8<>(getSqlBuilder());
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    // endregion

    // region [WHERE]
    public LQuery7<T1, T2, T3, T4, T5, T6, T7> where(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery7<T1, T2, T3, T4, T5, T6, T7> where(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public LQuery7<T1, T2, T3, T4, T5, T6, T7> orWhere(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery7<T1, T2, T3, T4, T5, T6, T7> orWhere(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> exists(Class<E> table, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),false);
        return this;
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> exists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> exists(LQuery<E> query, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),false);
        return this;
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> notExists(Class<E> table, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),true);
        return this;
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> notExists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery7<T1, T2, T3, T4, T5, T6, T7> notExists(LQuery<E> query, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),true);
        return this;
    }
    // endregion

    // region [ORDER BY]
    public <R> LQuery7<T1, T2, T3, T4, T5, T6, T7> orderBy(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery7<T1, T2, T3, T4, T5, T6, T7> orderBy(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery7<T1, T2, T3, T4, T5, T6, T7> orderBy(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery7<T1, T2, T3, T4, T5, T6, T7> orderBy(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> expr)
    {
        orderBy(expr.getTree(), true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public LQuery7<T1, T2, T3, T4, T5, T6, T7> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public LQuery7<T1, T2, T3, T4, T5, T6, T7> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [GROUP BY]
    public <Key> GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> groupBy(@Expr Func7<T1, T2, T3, T4, T5, T6, T7, Key> expr)
    {
        throw new NotCompiledException();
    }

    public <Key> GroupedQuery7<Key, T1, T2, T3, T4, T5, T6, T7> groupBy(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Key>> expr)
    {
        groupBy(expr.getTree());
        return new GroupedQuery7<>(getSqlBuilder());
    }
    // endregion

    // region [SELECT]
    public <R> EndQuery<R> select(@Recode Class<R> r)
    {
        return super.select(r);
    }

    public <R> LQuery<? extends R> select(@Expr Func7<T1, T2, T3, T4, T5, T6, T7, ? extends R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<? extends R> select(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, ? extends R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7,R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
    // endregion

    //region [OTHER]

    public LQuery7<T1, T2, T3, T4, T5, T6, T7> distinct()
    {
        distinct0(true);
        return this;
    }

    public LQuery7<T1, T2, T3, T4, T5, T6, T7> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }

    //endregion
}
