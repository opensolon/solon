package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlConditionsExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlWhereExpression;

import java.util.List;

public class SqlWhereExpression implements ISqlWhereExpression
{
    private final ISqlConditionsExpression conditions;

    SqlWhereExpression(ISqlConditionsExpression conditions)
    {
        this.conditions = conditions;
    }

    public void addCondition(ISqlExpression condition)
    {
        conditions.getConditions().add(condition);
    }

    public boolean isEmpty()
    {
        return conditions.isEmpty();
    }

    public ISqlConditionsExpression getConditions()
    {
        return conditions;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (isEmpty()) return "";
        return "WHERE " + getConditions().getSqlAndValue(config, values);
    }
}
