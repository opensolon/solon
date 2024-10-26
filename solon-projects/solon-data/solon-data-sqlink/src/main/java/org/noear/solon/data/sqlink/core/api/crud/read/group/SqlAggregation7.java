package org.noear.solon.data.sqlink.core.api.crud.read.group;

import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;
import io.github.kiryu1223.expressionTree.delegate.Func7;

import java.math.BigDecimal;

public abstract class SqlAggregation7<T1, T2, T3, T4, T5, T6, T7> implements IAggregation
{
    public <R> long count(Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal sum(Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal avg(Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R max(Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R min(Func7<T1, T2, T3, T4, T5, T6, T7, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

}
