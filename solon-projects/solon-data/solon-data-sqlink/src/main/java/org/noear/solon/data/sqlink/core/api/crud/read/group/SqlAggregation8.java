package org.noear.solon.data.sqlink.core.api.crud.read.group;

import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;
import io.github.kiryu1223.expressionTree.delegate.Func8;

import java.math.BigDecimal;

public abstract class SqlAggregation8<T1, T2, T3, T4, T5, T6, T7, T8> implements IAggregation
{
    public <R> long count(Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal sum(Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal avg(Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R max(Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R min(Func8<T1, T2, T3, T4, T5, T6, T7, T8, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

}
