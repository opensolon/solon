package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ISqlGroupByExpression extends ISqlExpression
{
    void setColumns(LinkedHashMap<String, ISqlExpression> columns);

    LinkedHashMap<String, ISqlExpression> getColumns();

    @Override
    default ISqlGroupByExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlGroupByExpression groupByExpression = factory.groupBy();
        for (Map.Entry<String, ISqlExpression> entry : getColumns().entrySet())
        {
            groupByExpression.getColumns().put(entry.getKey(), entry.getValue().copy(config));
        }
        return groupByExpression;
    }
}
