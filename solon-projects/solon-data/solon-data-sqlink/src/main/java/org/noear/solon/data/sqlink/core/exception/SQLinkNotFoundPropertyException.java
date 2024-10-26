package org.noear.solon.data.sqlink.core.exception;

import java.lang.reflect.Method;

public class SQLinkNotFoundPropertyException extends SQLinkException
{
    public SQLinkNotFoundPropertyException(Method method)
    {
        super(method.toGenericString());
    }

    public SQLinkNotFoundPropertyException(String name)
    {
        super(name);
    }
}
