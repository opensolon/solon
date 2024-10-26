package org.noear.solon.data.sqlink.core.api.crud.read.group;


import org.noear.solon.data.sqlink.core.exception.SqlFunctionInvokeException;

public interface IAggregation
{
    default long count()
    {
        throw new SqlFunctionInvokeException();
    }

    default long count(int i)
    {
        throw new SqlFunctionInvokeException();
    }
}
