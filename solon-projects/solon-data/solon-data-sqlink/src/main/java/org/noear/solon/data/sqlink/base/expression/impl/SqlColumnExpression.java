package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.IDialect;
import org.noear.solon.data.sqlink.base.expression.ISqlColumnExpression;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

import java.util.List;

public class SqlColumnExpression implements ISqlColumnExpression
{
    private final PropertyMetaData propertyMetaData;
    private final int tableIndex;

    public SqlColumnExpression(PropertyMetaData propertyMetaData, int tableIndex)
    {
        this.propertyMetaData = propertyMetaData;
        this.tableIndex = tableIndex;
    }

    @Override
    public PropertyMetaData getPropertyMetaData()
    {
        return propertyMetaData;
    }

    @Override
    public int getTableIndex()
    {
        return tableIndex;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        IDialect dbConfig = config.getDisambiguation();
        String t = "t" + getTableIndex();
        return dbConfig.disambiguation(t) + "." + dbConfig.disambiguation(getPropertyMetaData().getColumn());
    }
}
