package org.noear.solon.data.sqlink.core.api.crud.delete;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.delegate.Func2;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class LDelete<T> extends DeleteBase
{
    public LDelete(IConfig config, Class<T> c)
    {
        super(config, c);
    }

    //region [JOIN]
    public <Tn> LDelete2<T, Tn> innerJoin(Class<Tn> target, @Expr Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete2<T, Tn> innerJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.INNER, target, expr);
        return new LDelete2<>(getSqlBuilder());
    }

    public <Tn> LDelete2<T, Tn> leftJoin(Class<Tn> target, @Expr Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete2<T, Tn> leftJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.LEFT, target, expr);
        return new LDelete2<>(getSqlBuilder());
    }

    public <Tn> LDelete2<T, Tn> rightJoin(Class<Tn> target, @Expr Func2<T, Tn, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public <Tn> LDelete2<T, Tn> rightJoin(Class<Tn> target, ExprTree<Func2<T, Tn, Boolean>> expr)
    {
        join(JoinType.RIGHT, target, expr);
        return new LDelete2<>(getSqlBuilder());
    }
    //endregion

    // region [WHERE]
    public LDelete<T> where(@Expr Func1<T, Boolean> func)
    {
        throw new NotCompiledException();
    }

    public LDelete<T> where(ExprTree<Func1<T, Boolean>> expr)
    {
        where(expr.getTree());
        return this;
    }

    // endregion
}
