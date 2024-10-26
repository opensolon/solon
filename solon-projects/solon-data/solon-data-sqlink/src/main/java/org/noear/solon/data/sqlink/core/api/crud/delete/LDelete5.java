package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.DeleteSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func5;
import io.github.kiryu1223.expressionTree.delegate.Func6;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LDelete5<T1, T2, T3, T4, T5> extends DeleteBase
{
    public LDelete5(DeleteSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LDelete6<T1, T2, T3, T4, T5, Tn> innerJoin(Class<Tn> target, @Expr Func6<T1, T2, T3, T4, T5, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete6<T1, T2, T3, T4, T5, Tn> innerJoin(Class<Tn> target, ExprTree<Func6<T1, T2, T3, T4, T5, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LDelete6<>(getSqlBuilder());
    }

    public <Tn> LDelete6<T1, T2, T3, T4, T5, Tn> leftJoin(Class<Tn> target, @Expr Func6<T1, T2, T3, T4, T5, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete6<T1, T2, T3, T4, T5, Tn> leftJoin(Class<Tn> target, ExprTree<Func6<T1, T2, T3, T4, T5, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LDelete6<>(getSqlBuilder());
    }

    public <Tn> LDelete6<T1, T2, T3, T4, T5, Tn> rightJoin(Class<Tn> target, @Expr Func6<T1, T2, T3, T4, T5, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete6<T1, T2, T3, T4, T5, Tn> rightJoin(Class<Tn> target, ExprTree<Func6<T1, T2, T3, T4, T5, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LDelete6<>(getSqlBuilder());
    }
    //endregion

    // region [WHERE]
    public LDelete5<T1, T2, T3, T4, T5> where(@Expr Func5<T1, T2, T3, T4, T5, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LDelete5<T1, T2, T3, T4, T5> where(ExprTree<Func5<T1, T2, T3, T4, T5, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    // endregion

    public <R> LDelete5<T1, T2, T3, T4, T5> selectDelete(@Expr(Expr.BodyType.Expr) Func5<T1, T2, T3, T4, T5, R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LDelete5<T1, T2, T3, T4, T5> selectDelete(ExprTree<Func5<T1, T2, T3, T4, T5, R>> expr)
    {
        Class<?> returnType = expr.getTree().getReturnType();
        selectDeleteTable(returnType);
        return this;
    }
}
