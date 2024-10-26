package org.noear.solon.data.sqlink.core.sqlExt.sqlserver;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTypeExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.*;

public class SQLServerCastExtension extends BaseSqlExtension
{
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args)
    {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        ISqlExpression expression = args.get(1);
        ISqlTypeExpression typeExpression = (ISqlTypeExpression) expression;
        Class<?> type = typeExpression.getType();
        templates.add("CAST(");
        sqlExpressions.add(args.get(0));
        String unit;
        if (isChar(type))
        {
            unit = "NCHAR(1)";
        }
        else if (isString(type))
        {
            unit = "NVARCHAR";
        }
        else if (isTime(type))
        {
            unit = "TIME";
        }
        else if (isDate(type))
        {
            unit = "DATE";
        }
        else if (isDateTime(type))
        {
            unit = "DATETIME";
        }
        else if (isFloat(type) || isDouble(type))
        {
            unit = "DECIMAL(32,16)";
        }
        else if (isDecimal(type))
        {
            unit = "DECIMAL(32,18)";
        }
        else if (isByte(type))
        {
            unit = "TINYINT";
        }
        else if (isShort(type))
        {
            unit = "SMALLINT";
        }
        else if (isInt(type))
        {
            unit = "INT";
        }
        else if (isLong(type))
        {
            unit = "BIGINT";
        }
        else if (isBool(type))
        {
            unit = "BIT";
        }
        else
        {
            throw new UnsupportedOperationException("不支持的Java类型:" + type.getName());
        }
        templates.add(" AS " + unit + ")");
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
