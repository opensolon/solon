package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlConditionsExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlHavingExpression;

import java.util.List;

public class SqlHavingExpression implements ISqlHavingExpression
{
    private final ISqlConditionsExpression conditions;

    public SqlHavingExpression(ISqlConditionsExpression conditions)
    {
        this.conditions = conditions;
    }

    public void addCond(ISqlExpression condition)
    {
        System.out.println(condition);
        conditions.addCondition(condition);
    }

    @Override
    public ISqlConditionsExpression getConditions()
    {
        return conditions;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        if (isEmpty()) return "";
        return "HAVING " + getConditions().getSqlAndValue(config, values);
    }
}
