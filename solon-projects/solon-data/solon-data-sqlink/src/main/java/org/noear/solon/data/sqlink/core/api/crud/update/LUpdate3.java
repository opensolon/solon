package org.noear.solon.data.sqlink.core.api.crud.update;

import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action3;
import io.github.kiryu1223.expressionTree.delegate.Func3;
import io.github.kiryu1223.expressionTree.delegate.Func4;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate3<T1, T2, T3> extends UpdateBase
{
    public LUpdate3(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [JOIN]
    public <Tn> LUpdate4<T1, T2, T3, Tn> innerJoin(Class<Tn> target, @Expr Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate4<T1, T2, T3, Tn> innerJoin(Class<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LUpdate4<>(getSqlBuilder());
    }

    public <Tn> LUpdate4<T1, T2, T3, Tn> leftJoin(Class<Tn> target, @Expr Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate4<T1, T2, T3, Tn> leftJoin(Class<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LUpdate4<>(getSqlBuilder());
    }

    public <Tn> LUpdate4<T1, T2, T3, Tn> rightJoin(Class<Tn> target, @Expr Func4<T1, T2, T3, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LUpdate4<T1, T2, T3, Tn> rightJoin(Class<Tn> target, ExprTree<Func4<T1, T2, T3, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LUpdate4<>(getSqlBuilder());
    }
    //endregion

    //region [SET]
    public LUpdate3<T1, T2, T3> set(@Expr Action3<T1, T2, T3> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate3<T1, T2, T3> set(ExprTree<Action3<T1, T2, T3>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate3<T1, T2, T3> where(@Expr Func3<T1, T2, T3, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate3<T1, T2, T3> where(ExprTree<Func3<T1, T2, T3, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
