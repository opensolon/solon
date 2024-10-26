package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.metaData.IConverter;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;
import org.noear.solon.data.sqlink.base.sqlExt.ISqlKeywords;

import java.util.List;

import static com.sun.jmx.mbeanserver.Util.cast;

public class SqlSingleValueExpression extends SqlValueExpression implements ISqlSingleValueExpression
{
    private final Object value;

    SqlSingleValueExpression(Object value)
    {
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (getValue() == null)
        {
            return "NULL";
        }
        else if (getValue() instanceof ISqlKeywords)
        {
            ISqlKeywords keywords = (ISqlKeywords) getValue();
            return keywords.getKeyword(config);
        }
        else
        {
            if (values != null) values.add(getValue());
            return "?";
        }
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values, IConverter<?, ?> converter, PropertyMetaData propertyMetaData)
    {
        if (getValue() == null)
        {
            return "NULL";
        }
        else
        {
            if (values != null) values.add(converter.toDb(cast(getValue()), propertyMetaData));
            return "?";
        }
    }
}
