package org.noear.solon.data.sqlink.core.sqlExt.oracle;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.core.exception.SQLinkIntervalException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OracleAddOrSubDateExtension extends BaseSqlExtension
{
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args)
    {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        if (sqlFunc.getParameterCount() == 2)
        {
            templates.add("(");
            sqlExpressions.add(args.get(0));
            ISqlExpression num = args.get(1);
            if (num instanceof ISqlSingleValueExpression)
            {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                if (sqlFunc.getName().equals("addDate"))
                {
                    templates.add(" + INTERVAL '" + valueExpression.getValue() + "' DAY)");
                }
                else
                {
                    templates.add(" - INTERVAL '" + valueExpression.getValue() + "' DAY)");
                }
            }
            else
            {
                throw new SQLinkIntervalException(DbType.Oracle);
            }
        }
        else
        {
            templates.add("(");
            sqlExpressions.add(args.get(0));
            sqlExpressions.add(args.get(1));
            ISqlExpression num = args.get(2);
            if (num instanceof ISqlSingleValueExpression)
            {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                if (sqlFunc.getName().equals("addDate"))
                {
                    templates.add(" + INTERVAL '" + valueExpression.getValue() + "' ");
                }
                else
                {
                    templates.add(" - INTERVAL '" + valueExpression.getValue() + "' ");
                }
                templates.add(")");
            }
            else
            {
                throw new SQLinkIntervalException(DbType.Oracle);
            }
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}