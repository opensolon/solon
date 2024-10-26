package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LDelete7<T1, T2, T3, T4, T5, T6, T7> extends DeleteBase
{
    public LDelete7(DeleteSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LDelete8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LDelete8<>(getSqlBuilder());
    }

    public <Tn> LDelete8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LDelete8<>(getSqlBuilder());
    }

    public <Tn> LDelete8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LDelete8<>(getSqlBuilder());
    }
    //endregion

    // region [WHERE]
    public LDelete7<T1, T2, T3, T4, T5, T6, T7> where(@Expr Func7<T1, T2, T3, T4, T5, T6, T7, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LDelete7<T1, T2, T3, T4, T5, T6, T7> where(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    // endregion

    public <R> LDelete7<T1, T2, T3, T4, T5, T6, T7> selectDelete(@Expr(Expr.BodyType.Expr) Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LDelete7<T1, T2, T3, T4, T5, T6, T7> selectDelete(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, R>> expr)
    {
        Class<?> returnType = expr.getTree().getReturnType();
        selectDeleteTable(returnType);
        return this;
    }
}
