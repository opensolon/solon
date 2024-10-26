package org.noear.solon.data.sqlink.core.api.crud.read;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.core.exception.NotCompiledException;
import io.github.kiryu1223.expressionTree.delegate.Func0;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

public class EmptyQuery extends QueryBase
{
    public EmptyQuery(IConfig config)
    {
        super(config, Empty.class);
    }

    public <R> LQuery<? extends R> select(@Expr Func0<? extends R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> LQuery<? extends R> select(ExprTree<Func0<? extends R>> expr)
    {
        boolean single = select(expr.getTree());
        singleCheck(single);
        return new LQuery<>(boxedQuerySqlBuilder());
    }

    public <R> EndQuery<R> endSelect(@Expr(Expr.BodyType.Expr) Func0<R> expr)
    {
        throw new NotCompiledException();
    }

    public <R> EndQuery<R> endSelect(ExprTree<Func0<R>> expr)
    {
        select(expr.getTree());
        return new EndQuery<>(getSqlBuilder());
    }
}
