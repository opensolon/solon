package org.noear.solon.data.sqlink.core.visitor;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlGroupByExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlQueryableExpression;
import io.github.kiryu1223.expressionTree.expressions.FieldSelectExpression;

import java.util.Map;

public class HavingVisitor extends SqlVisitor
{
    private final ISqlQueryableExpression queryable;

    public HavingVisitor(IConfig config, ISqlQueryableExpression queryable)
    {
        super(config);
        this.queryable = queryable;
    }

    @Override
    public ISqlExpression visit(FieldSelectExpression fieldSelect)
    {
        if (ExpressionUtil.isGroupKey(parameters, fieldSelect)) // g.key
        {
            ISqlGroupByExpression groupBy = queryable.getGroupBy();
            return groupBy.getColumns().get("key");
        }
        else if (ExpressionUtil.isGroupKey(parameters, fieldSelect.getExpr())) // g.key.xxx
        {
            Map<String, ISqlExpression> columns = queryable.getGroupBy().getColumns();
            return columns.get(fieldSelect.getField().getName());
        }
        else
        {
            return super.visit(fieldSelect);
        }
    }

    @Override
    protected SqlVisitor getSelf()
    {
        return new HavingVisitor(config, queryable);
    }
}
