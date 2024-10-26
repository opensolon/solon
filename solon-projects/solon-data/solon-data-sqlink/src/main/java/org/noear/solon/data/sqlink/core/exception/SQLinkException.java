package org.noear.solon.data.sqlink.core.exception;

public class SQLinkException extends RuntimeException
{
    public SQLinkException()
    {
    }

    public SQLinkException(String message)
    {
        super(message);
    }

    public SQLinkException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SQLinkException(Throwable cause)
    {
        super(cause);
    }

    public SQLinkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
