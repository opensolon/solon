package org.noear.solon.data.sqlink.core.exception;

import org.noear.solon.data.sqlink.base.DbType;

public class SQLinkIntervalException extends SQLinkException
{
    public SQLinkIntervalException(DbType type)
    {
        super(type.name() + "下的date加减运算函数必须为字面量或者java引用（不可以是数据库字段引用）");
    }
}
