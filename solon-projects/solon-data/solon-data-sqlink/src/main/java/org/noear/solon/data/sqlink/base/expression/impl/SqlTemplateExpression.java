package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTemplateExpression;

import java.util.List;

public class SqlTemplateExpression implements ISqlTemplateExpression
{
    private final List<String> functions;
    private final List<? extends ISqlExpression> expressions;

    SqlTemplateExpression(List<String> functions, List<? extends ISqlExpression> expressions)
    {
        this.functions = functions;
        this.expressions = expressions;
    }

    @Override
    public List<String> getFunctions()
    {
        return functions;
    }

    @Override
    public List<? extends ISqlExpression> getExpressions()
    {
        return expressions;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getFunctions().size(); i++)
        {
            String function = getFunctions().get(i);
            sb.append(function);
            if (i < getExpressions().size())
            {
                ISqlExpression expression = getExpressions().get(i);
                sb.append(expression.getSqlAndValue(config, values));
            }
        }
        return sb.toString();
    }
}
