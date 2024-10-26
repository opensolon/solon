package org.noear.solon.data.sqlink.core.api.crud.read.group;

import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;
import io.github.kiryu1223.expressionTree.delegate.Func6;

import java.math.BigDecimal;

public abstract class SqlAggregation6<T1, T2, T3, T4, T5, T6> implements IAggregation
{
    public <R> long count(Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal sum(Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal avg(Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R max(Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R min(Func6<T1, T2, T3, T4, T5, T6, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

}
