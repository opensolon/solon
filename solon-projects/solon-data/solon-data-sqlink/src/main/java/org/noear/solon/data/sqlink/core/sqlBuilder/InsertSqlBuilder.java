package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.List;

public class InsertSqlBuilder implements ISqlBuilder
{
    private final IConfig config;

    public InsertSqlBuilder(IConfig config)
    {
        this.config = config;
    }


    @Override
    public String getSql()
    {
        return "";
    }

    @Override
    public String getSqlAndValue(List<Object> values)
    {
        return "";
    }

    public IConfig getConfig()
    {
        return config;
    }
}
