package org.noear.solon.data.sqlink.core.exception;

public class SqlCalculatesInvokeException extends RuntimeException
{
    public SqlCalculatesInvokeException()
    {
        super("SqlCalculate cannot be called from outside an expression");
    }
}
