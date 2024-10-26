package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlBinaryExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.expression.SqlOperator;

import java.util.List;

public class SqlBinaryExpression implements ISqlBinaryExpression
{
    private final SqlOperator operator;
    private final ISqlExpression left;
    private final ISqlExpression right;

    public SqlBinaryExpression(SqlOperator operator, ISqlExpression left, ISqlExpression right)
    {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public SqlOperator getOperator()
    {
        return operator;
    }

    @Override
    public ISqlExpression getLeft()
    {
        return left;
    }

    @Override
    public ISqlExpression getRight()
    {
        return right;
    }

    @Override
    public String getSqlAndValue(IConfig config, List<Object> values)
    {
        SqlOperator operator = getOperator();
        StringBuilder sb = new StringBuilder();
        sb.append(getLeft().getSqlAndValue(config, values));
        sb.append(" ");
        // (= NULL) => (IS NULL)
        if (operator == SqlOperator.EQ
                && getRight() instanceof ISqlSingleValueExpression
                && ((ISqlSingleValueExpression) getRight()).getValue() == null)
        {
            sb.append(SqlOperator.IS.getOperator());
        }
        else
        {
            sb.append(operator.getOperator());
        }
        sb.append(" ");
        if (operator == SqlOperator.IN)
        {
            sb.append("(");
            sb.append(getRight().getSqlAndValue(config, values));
            sb.append(")");
        }
        else
        {
            sb.append(getRight().getSqlAndValue(config, values));
        }
        return sb.toString();
    }
}
