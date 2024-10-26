package org.noear.solon.data.sqlink.core.exception;

public class NotCompiledException extends RuntimeException
{
    public NotCompiledException()
    {
        super("Please clean and recompile the project");
    }
}
