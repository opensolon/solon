package org.noear.solon.data.sqlink.core.exception;

import org.noear.solon.data.sqlink.base.DbType;

public class SQLinkLimitNotFoundOrderByException extends SQLinkException
{
    public SQLinkLimitNotFoundOrderByException(DbType dbType)
    {
        super(dbType.name() + "数据库下进行的limit操作需要声明order by的字段，或者为表类指定一个主键");
    }
}
