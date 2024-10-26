package org.noear.solon.data.sqlink.core.api.crud.update;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action8;
import io.github.kiryu1223.expressionTree.delegate.Func8;
import io.github.kiryu1223.expressionTree.delegate.Func9;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate8<T1, T2, T3, T4, T5, T6, T7, T8> extends UpdateBase
{
    public LUpdate8(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LUpdate9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(Class<Tn> target, @Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> innerJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LUpdate9<>(getSqlBuilder());
    }

    public <Tn> LUpdate9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(Class<Tn> target, @Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> leftJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LUpdate9<>(getSqlBuilder());
    }

    public <Tn> LUpdate9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(Class<Tn> target, @Expr Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate9<T1, T2, T3, T4, T5, T6, T7, T8, Tn> rightJoin(Class<Tn> target, ExprTree<Func9<T1, T2, T3, T4, T5, T6, T7, T8, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate9<>(getSqlBuilder());
    }
    //endregion

    //region [SET]
    public LUpdate8<T1, T2, T3, T4, T5, T6, T7, T8> set(@Expr Action8<T1, T2, T3, T4, T5, T6, T7, T8> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate8<T1, T2, T3, T4, T5, T6, T7, T8> set(ExprTree<Action8<T1, T2, T3, T4, T5, T6, T7, T8>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate8<T1, T2, T3, T4, T5, T6, T7, T8> where(@Expr Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate8<T1, T2, T3, T4, T5, T6, T7, T8> where(ExprTree<Func8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
