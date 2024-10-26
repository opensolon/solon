package org.noear.solon.data.sqlink.core.visitor;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSetExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSetsExpression;
import io.github.kiryu1223.expressionTree.expressions.BlockExpression;
import io.github.kiryu1223.expressionTree.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class SetVisitor extends SqlVisitor
{
    public SetVisitor(IConfig config)
    {
        super(config);
    }

    public SetVisitor(IConfig config, int offset)
    {
        super(config, offset);
    }

    @Override
    public ISqlExpression visit(BlockExpression blockExpression)
    {
        List<ISqlSetExpression> sqlSetExpressions=new ArrayList<>();
        for (Expression expression : blockExpression.getExpressions())
        {
            ISqlExpression visit = visit(expression);
            if(visit instanceof ISqlSetExpression)
            {
                sqlSetExpressions.add((ISqlSetExpression) visit);
            }
        }
        ISqlSetsExpression sets = factory.sets();
        sets.addSet(sqlSetExpressions);
        return sets;
    }

    @Override
    protected SqlVisitor getSelf()
    {
        return new SetVisitor(config);
    }
}
