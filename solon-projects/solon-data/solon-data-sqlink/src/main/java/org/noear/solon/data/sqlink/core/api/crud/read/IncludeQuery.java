package org.noear.solon.data.sqlink.core.api.crud.read;

import org.noear.solon.data.sqlink.base.toBean.Include.IncludeSet;
import org.noear.solon.data.sqlink.core.sqlBuilder.QuerySqlBuilder;
import io.github.kiryu1223.expressionTree.delegate.Action1;
import io.github.kiryu1223.expressionTree.delegate.Func1;
import io.github.kiryu1223.expressionTree.expressions.annos.Expr;
import io.github.kiryu1223.expressionTree.expressions.ExprTree;

import java.util.Collection;

public class IncludeQuery<T, TPreviousProperty> extends LQuery<T>
{
    private final IncludeSet curIncludeSet;

    public IncludeQuery(QuerySqlBuilder sqlBuilder)
    {
        this(sqlBuilder, sqlBuilder.getLastIncludeSet());
    }

    public IncludeQuery(QuerySqlBuilder sqlBuilder, IncludeSet includeSet)
    {
        super(sqlBuilder);
        this.curIncludeSet = includeSet;
    }

    public <TProperty> IncludeQuery<T, TProperty> thenInclude(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, TProperty> expr, @Expr(Expr.BodyType.Expr) Func1<TProperty, Boolean> cond)
    {
        throw new RuntimeException();
    }

    public <TProperty> IncludeQuery<T, TProperty> thenInclude(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, TProperty> expr)
    {
        throw new RuntimeException();
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, Collection<TProperty>> expr, @Expr(Expr.BodyType.Expr) Func1<TProperty, Boolean> cond)
    {
        throw new RuntimeException();
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, Collection<TProperty>> expr)
    {
        throw new RuntimeException();
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludesByCond(@Expr(Expr.BodyType.Expr) Func1<TPreviousProperty, Collection<TProperty>> expr, Action1<IncludeCond<TProperty>> action)
    {
        throw new RuntimeException();
    }

    public <TProperty> IncludeQuery<T, TProperty> thenInclude(ExprTree<Func1<TPreviousProperty, TProperty>> expr, ExprTree<Func1<TProperty, Boolean>> cond)
    {
        include(expr.getTree(), cond.getTree(), curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenInclude(ExprTree<Func1<TPreviousProperty, TProperty>> expr)
    {
        include(expr.getTree(), null, curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(ExprTree<Func1<TPreviousProperty, Collection<TProperty>>> expr, ExprTree<Func1<TProperty, Boolean>> cond)
    {
        include(expr.getTree(), cond.getTree(), curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludes(ExprTree<Func1<TPreviousProperty, Collection<TProperty>>> expr)
    {
        include(expr.getTree(), null, curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }

    public <TProperty> IncludeQuery<T, TProperty> thenIncludesByCond(ExprTree<Func1<TPreviousProperty, Collection<TProperty>>> expr, Action1<IncludeCond<TProperty>> action)
    {
        includeByCond(expr.getTree(), action, curIncludeSet.getIncludeSets());
        return new IncludeQuery<>(getSqlBuilder(), curIncludeSet.getLastIncludeSet());
    }
}
