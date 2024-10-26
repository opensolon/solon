package org.noear.solon.data.sqlink.core.api.crud.update;

import org.noear.solon.data.sqlink.core.sqlBuilder.UpdateSqlBuilder;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Action10;
import io.github.kiryu1223.expressionTree.delegate.Func10;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends UpdateBase
{
    public LUpdate10(UpdateSqlBuilder sqlBuilder)
    {
        super(sqlBuilder);
    }

    //region [SET]
    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(@Expr Action10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> action)
    {
        throw new NotCompiledException();
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> set(ExprTree<Action10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> expr)
    {
        set(expr.getTree());
        return this;
    }
    //endregion

    //region [WHERE]
    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> where(@Expr Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LUpdate10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> where(ExprTree<Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }
    //endregion
}
