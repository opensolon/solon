package org.noear.solon.data.sqlink.core.sqlExt.oracle;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class OracleJoinExtension extends BaseSqlExtension
{
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args)
    {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        ISqlExpression separator = args.get(0);
        templates.add("(");
        for (int i = 1; i < args.size(); i++)
        {
            sqlExpressions.add(args.get(i));
            if (i < args.size() - 1)
            {
                templates.add("||");
                sqlExpressions.add(separator);
                templates.add("||");
            }
        }
        templates.add(")");
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
