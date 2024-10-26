package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.IConfig;

import java.util.ArrayList;
import java.util.List;

public interface ISqlTemplateExpression extends ISqlExpression
{
    List<String> getFunctions();

    List<? extends ISqlExpression> getExpressions();

    @Override
    default ISqlTemplateExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> newFunctions = new ArrayList<>(getFunctions());
        List<? extends ISqlExpression> newExpressions = new ArrayList<>(getExpressions());
        return factory.template(newFunctions, newExpressions);
    }
}
