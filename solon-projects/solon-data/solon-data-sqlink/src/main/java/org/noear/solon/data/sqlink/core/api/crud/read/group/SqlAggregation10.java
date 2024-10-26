package org.noear.solon.data.sqlink.core.api.crud.read.group;

import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;
import io.github.kiryu1223.expressionTree.delegate.Func10;

import java.math.BigDecimal;

public abstract class SqlAggregation10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> implements IAggregation
{
    public <R> long count(Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal sum(Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> BigDecimal avg(Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R max(Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

    public <R> R min(Func10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> expr)
    {
        throw new SqlFunctionInvokeException();
    }

}
