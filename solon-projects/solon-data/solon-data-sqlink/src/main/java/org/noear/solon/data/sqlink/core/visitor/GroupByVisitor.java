package org.noear.solon.data.sqlink.core.visitor;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import io.github.kiryu1223.expressionTree.expressions.*;

import java.util.LinkedHashMap;


public class GroupByVisitor extends SqlVisitor
{
    public GroupByVisitor(IConfig config)
    {
        super(config);
    }

    @Override
    public ISqlExpression visit(NewExpression newExpression)
    {
        BlockExpression classBody = newExpression.getClassBody();
        if (classBody == null)
        {
            return super.visit(newExpression);
        }
        else
        {
            LinkedHashMap<String, ISqlExpression> contextMap = new LinkedHashMap<>();
            for (Expression expression : classBody.getExpressions())
            {
                if (expression.getKind() == Kind.Variable)
                {
                    VariableExpression variableExpression = (VariableExpression) expression;
                    String name = variableExpression.getName();
                    ISqlExpression sqlExpression = visit(variableExpression.getInit());
                    contextMap.put(name, sqlExpression);
                }
            }
            return factory.groupBy(contextMap);
        }
    }

    @Override
    protected GroupByVisitor getSelf()
    {
        return new GroupByVisitor(config);
    }
}
