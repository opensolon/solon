package org.noear.solon.data.sqlink.core.api.crud.read;


import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import org.noear.solon.data.sqlink.core.api.crud.read.group.GroupedQuery8;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.delegate.Func9;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;
import io.github.kiryu1223.expressionTree.expressions.annos.Recode;

public class LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> extends QueryBase
{
    // region [INIT]

    public LQuery8(QuerySqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    // endregion

    //region [JOIN]

    @Override
    protected <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> joinNewQuery()
    {
        return new LQuery9<>(getSqlBuilder());
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(LQuery<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(LQuery<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(Class<Tn> target, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(LQuery<Tn> target, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LQuery9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(LQuery<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return joinNewQuery();
    }

    // endregion

    // region [WHERE]
    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> where(@Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> where(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> orWhere(@Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> orWhere(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean>> expr)
    {
        orWhere(expr.getTree());
        return this;
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> exists(Class<E> table, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> exists(Class<E> table, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),false);
        return this;
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> exists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> exists(LQuery<E> query, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),false);
        return this;
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> notExists(Class<E> table, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> notExists(Class<E> table, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean>> expr)
    {
        exists(table, expr.getTree(),true);
        return this;
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> notExists(LQuery<E> query, @Expr(Expr.BodyType.Expr) Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <E> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> notExists(LQuery<E> query, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, E, Boolean>> expr)
    {
        exists(query, expr.getTree(),true);
        return this;
    }
    // endregion

    // region [ORDER BY]
    public <R> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> orderBy(@Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr, boolean asc)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> orderBy(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, R>> expr, boolean asc)
    {
        orderBy(expr.getTree(), asc);
        return this;
    }

    public <R> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> orderBy(@Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> orderBy(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, R>> expr)
    {
        orderBy(expr.getTree(), true);
        return this;
    }
    // endregion

    // region [LIMIT]
    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> limit(long rows)
    {
        limit0(rows);
        return this;
    }

    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> limit(long offset, long rows)
    {
        limit0(offset, rows);
        return this;
    }
    // endregion

    // region [GROUP BY]
    public <Key> GroupedQuery8<Key, T1, T2, T3, T4, T5, T6, T7, T8> groupBy(@Expr Func8<T1, T2, T3, T4, T5, T6, T7, T8, Key> expr)
    {
        throw new NotCompiledException();
    }

    public <Key> GroupedQuery8<Key, T1, T2, T3, T4, T5, T6, T7, T8> groupBy(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, Key>> expr)
    {
        groupBy(expr.getTree());
        return new GroupedQuery8<>(getSqlBuilder());
    }
    // endregion

    // region [SELECT]
    public <R> EndQuery<R> select(@Recode Class<R> r)
    {
        return super.select(r);
    }

    public <R> LQuery<? extends R> select(@Expr Func8<T1, T2, T3, T4, T5, T6, T7, T8, ? extends R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<? extends R> select(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, ? extends R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
    // endregion

    //region [OTHER]

    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> distinct()
    {
        distinct0(true);
        return this;
    }

    public LQuery8<T1, T2, T3, T4, T5, T6, T7, T8> distinct(boolean condition)
    {
        distinct0(condition);
        return this;
    }

    //endregion
}
