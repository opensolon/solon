package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.metaData.IConverter;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

import java.util.List;

public interface ISqlSingleValueExpression extends ISqlValueExpression
{
    Object getValue();

    String getSqlAndValue(IConfig config, List<Object> values, IConverter<?, ?> converter, PropertyMetaData propertyMetaData);

    @Override
    default ISqlSingleValueExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.value(getValue());
    }
}
