package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlColumnExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSetExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

import java.util.List;

public class SqlSetExpression implements ISqlSetExpression
{
    private final ISqlColumnExpression column;
    private final ISqlExpression value;

    SqlSetExpression(ISqlColumnExpression column, ISqlExpression value)
    {
        this.column = column;
        this.value = value;
    }

    @Override
    public ISqlColumnExpression getColumn()
    {
        return column;
    }

    @Override
    public ISqlExpression getValue()
    {
        return value;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        String set = getColumn().getSqlAndValue(config, values) + " = ";
        PropertyMetaData propertyMetaData = getColumn().getPropertyMetaData();
        if (propertyMetaData.hasConverter() && getValue() instanceof ISqlSingleValueExpression)
        {
            ISqlSingleValueExpression sqlSingleValueExpression = (ISqlSingleValueExpression) getValue();
            return set + sqlSingleValueExpression.getSqlAndValue(config, values, propertyMetaData.getConverter(), propertyMetaData);
        }
        else
        {
            return set + getValue().getSqlAndValue(config, values);
        }
    }
}
