package org.noear.solon.data.sqlink.core.sqlExt.pgsql;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.core.exception.SQLinkIntervalException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLAddOrSubDateExtension extends BaseSqlExtension
{
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args)
    {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        boolean isPlus = sqlFunc.getName().equals("addDate");
        if (isPlus)
        {
            templates.add("DATE_ADD(");
        }
        else
        {
            templates.add("DATE_SUBTRACT(");
        }
        sqlExpressions.add(args.get(0));
        if (sqlFunc.getParameterCount() == 2)
        {
            ISqlExpression num = args.get(1);
            if (num instanceof ISqlSingleValueExpression)
            {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                templates.add(",INTERVAL '" + valueExpression.getValue() + "' DAY)");
            }
            else
            {
                throw new SQLinkIntervalException(DbType.PostgreSQL);
            }
        }
        else
        {
            ISqlExpression num = args.get(2);
            if (num instanceof ISqlSingleValueExpression)
            {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                templates.add(",INTERVAL '" + valueExpression.getValue() + "' ");
                sqlExpressions.add(args.get(1));
                templates.add(")");
            }
            else
            {
                throw new SQLinkIntervalException(DbType.PostgreSQL);
            }
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
