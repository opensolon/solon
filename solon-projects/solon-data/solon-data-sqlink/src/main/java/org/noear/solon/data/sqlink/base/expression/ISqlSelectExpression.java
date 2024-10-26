package org.noear.solon.data.sqlink.base.expression;


import org.noear.solon.data.sqlink.base.IConfig;

import java.util.ArrayList;
import java.util.List;

public interface ISqlSelectExpression extends ISqlExpression
{
    List<ISqlExpression> getColumns();

    boolean isSingle();

    Class<?> getTarget();

    boolean isDistinct();

    void setColumns(List<ISqlExpression> columns);

    void setSingle(boolean single);

    void setDistinct(boolean distinct);

    void setTarget(Class<?> target);

    @Override
    default ISqlSelectExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<ISqlExpression> newColumns = new ArrayList<>(getColumns().size());
        for (ISqlExpression column : getColumns())
        {
            newColumns.add(column.copy(config));
        }
        return factory.select(newColumns, getTarget(), isSingle(), isDistinct());
    }
}
