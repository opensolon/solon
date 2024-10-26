package org.noear.solon.data.sqlink.core.exception;

import org.noear.solon.data.sqlink.base.DbType;

public class SqlFuncExtNotFoundException extends RuntimeException
{
    public SqlFuncExtNotFoundException(DbType type)
    {
        super("No corresponding SqlExtensionExpression annotation found for database type: " + type);
    }
}
