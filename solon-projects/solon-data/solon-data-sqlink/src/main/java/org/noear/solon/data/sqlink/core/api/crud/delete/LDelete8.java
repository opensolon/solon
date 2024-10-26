package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.delegate.Func9;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LDelete8<T1, T2, T3, T4, T5, T6, T7, T8> extends DeleteBase
{
    public LDelete8(DeleteSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LDelete9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(Class<Tn> target, @Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LDelete9<>(getSqlBuilder());
    }

    public <Tn> LDelete9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(Class<Tn> target, @Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LDelete9<>(getSqlBuilder());
    }

    public <Tn> LDelete9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(Class<Tn> target, @Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LDelete9<>(getSqlBuilder());
    }
    //endregion

    // region [WHERE]
    public LDelete8<T1, T2, T3, T4, T5, T6, T7, T8> where(@Expr Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LDelete8<T1, T2, T3, T4, T5, T6, T7, T8> where(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    // endregion

    public <R> LDelete8<T1, T2, T3, T4, T5, T6, T7, T8> selectDelete(@Expr(Expr.BodyType.Expr) Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LDelete8<T1, T2, T3, T4, T5, T6, T7, T8> selectDelete(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, R>> expr)
    {
        Class<?> returnType = expr.getTree().getReturnType();
        selectDeleteTable(returnType);
        return this;
    }
}
