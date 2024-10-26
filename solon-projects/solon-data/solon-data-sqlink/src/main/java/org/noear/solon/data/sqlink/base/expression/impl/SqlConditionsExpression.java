package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlConditionsExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;

import java.util.ArrayList;
import java.util.List;

public class SqlConditionsExpression implements ISqlConditionsExpression
{
    private final List<ISqlExpression> conditions = new ArrayList<>();

    @Override
    public List<ISqlExpression> getConditions()
    {
        return conditions;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        List<String> whereStr = new ArrayList<>(getConditions().size());
        for (ISqlExpression expression : getConditions())
        {
            whereStr.add(expression.getSqlAndValue(config, values));
        }
        return String.join(" AND ", whereStr);
    }
}
