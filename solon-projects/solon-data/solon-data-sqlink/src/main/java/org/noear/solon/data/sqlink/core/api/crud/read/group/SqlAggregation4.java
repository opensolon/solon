package org.noear.solon.data.sqlink.core.api.crud.read.group;

import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;
import io.github.kiryu1223.expressionTree.delegate.Func4;

import java.math.BigDecimal;

public abstract class SqlAggregation4<T1, T2, T3, T4> implements IAggregation
{
    public <R> long count(Func4<T1, T2, T3, T4, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal sum(Func4<T1, T2, T3, T4, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal avg(Func4<T1, T2, T3, T4, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R max(Func4<T1, T2, T3, T4, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R min(Func4<T1, T2, T3, T4, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

}
