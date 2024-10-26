package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func6;
import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LDelete6<T1, T2, T3, T4, T5, T6> extends DeleteBase
{
    public LDelete6(DeleteSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LDelete7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(Class<Tn> target, @Expr Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete7<T1, T2, T3, T4, T5, T6, Tn> innerJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LDelete7<>(getSqlBuilder());
    }

    public <Tn> LDelete7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(Class<Tn> target, @Expr Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete7<T1, T2, T3, T4, T5, T6, Tn> leftJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LDelete7<>(getSqlBuilder());
    }

    public <Tn> LDelete7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(Class<Tn> target, @Expr Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete7<T1, T2, T3, T4, T5, T6, Tn> rightJoin(Class<Tn> target, ExprTree<Func7<T1, T2, T3, T4, T5, T6, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LDelete7<>(getSqlBuilder());
    }
    //endregion

    // region [WHERE]
    public LDelete6<T1, T2, T3, T4, T5, T6> where(@Expr Func6<T1, T2, T3, T4, T5, T6, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LDelete6<T1, T2, T3, T4, T5, T6> where(ExprTree<Func6<T1, T2, T3, T4, T5, T6, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    // endregion

    public <R> LDelete6<T1, T2, T3, T4, T5, T6> selectDelete(@Expr(Expr.BodyType.Expr) Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LDelete6<T1, T2, T3, T4, T5, T6> selectDelete(ExprTree<Func6<T1, T2, T3, T4, T5, T6, R>> expr)
    {
        Class<?> returnType = expr.getTree().getReturnType();
        selectDeleteTable(returnType);
        return this;
    }
}
