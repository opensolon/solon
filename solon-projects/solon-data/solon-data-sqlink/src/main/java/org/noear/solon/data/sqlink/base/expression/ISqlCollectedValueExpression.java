package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ISqlCollectedValueExpression extends ISqlValueExpression
{
    Collection<Object> getCollection();

    void setDelimiter(String delimiter);

    String getDelimiter();

    @Override
    default ISqlCollectedValueExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<Object> newValues = new ArrayList<>(getCollection());
        ISqlCollectedValueExpression value = factory.value(newValues);
        value.setDelimiter(getDelimiter());
        return value;
    }
}
