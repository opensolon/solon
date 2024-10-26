package org.noear.solon.data.sqlink.core.exception;

public class SqlFunctionInvokeException extends RuntimeException
{
    public SqlFunctionInvokeException()
    {
        super("SqlFunction cannot be called from outside an expression");
    }
}
