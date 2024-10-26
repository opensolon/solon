package org.noear.solon.data.sqlink.core.sqlExt.sqlite;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.core.exception.SQLinkIntervalException;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqliteAddOrSubDateExtension extends BaseSqlExtension
{
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args)
    {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        boolean isPlus = sqlFunc.getName().equals("addDate");
        if (sqlFunc.getParameterTypes()[0] == LocalDate.class)
        {
            templates.add("DATE(");
        }
        else
        {
            templates.add("DATETIME(");
        }
        sqlExpressions.add(args.get(0));
        if (sqlFunc.getParameterCount() == 2)
        {
            ISqlExpression num = args.get(1);
            if (num instanceof ISqlSingleValueExpression)
            {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                templates.add(",'" + (isPlus ? "" : "-") + valueExpression.getValue() + " DAY')");
            }
            else
            {
                throw new SQLinkIntervalException(DbType.SQLite);
            }
        }
        else
        {
            ISqlExpression num = args.get(2);
            if (num instanceof ISqlSingleValueExpression)
            {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                templates.add(",'" + (isPlus ? "" : "-") + valueExpression.getValue()+" ");
                sqlExpressions.add(args.get(1));
                templates.add("')");
            }
            else
            {
                throw new SQLinkIntervalException(DbType.SQLite);
            }
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
