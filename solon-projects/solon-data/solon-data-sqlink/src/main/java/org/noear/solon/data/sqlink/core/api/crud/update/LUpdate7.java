package org.noear.solon.data.sqlink.core.api.crud.update;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action7;
import io.github.kiryu1223.expressionTree.delegate.Func7;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate7<T1, T2, T3, T4, T5, T6, T7> extends UpdateBase
{
    public LUpdate7(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> innerJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LUpdate8<>(getSqlBuilder());
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> leftJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LUpdate8<>(getSqlBuilder());
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, @Expr Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate8<T1, T2, T3, T4, T5, T6, T7, Tn> rightJoin(Class<Tn> target, ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate8<>(getSqlBuilder());
    }
    //endregion

    //region [SET]
    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> set(@Expr Action7<T1, T2, T3, T4, T5, T6, T7> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> set(ExprTree<Action7<T1, T2, T3, T4, T5, T6, T7>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> where(@Expr Func7<T1, T2, T3, T4, T5, T6, T7, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate7<T1, T2, T3, T4, T5, T6, T7> where(ExprTree<Func7<T1, T2, T3, T4, T5, T6, T7, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
