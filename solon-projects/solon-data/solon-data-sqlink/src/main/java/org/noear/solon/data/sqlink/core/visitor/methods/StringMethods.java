package org.noear.solon.data.sqlink.core.visitor.methods;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.core.exception.SQLinkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringMethods
{
    public static ISqlBinaryExpression contains(IConfig config, ISqlExpression left, ISqlExpression right)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        switch (config.getDbType())
        {
            case Oracle:
            case SQLite:
                functions = Arrays.asList("('%'||", "||'%')");
                break;
            default:
                functions = Arrays.asList("CONCAT('%',", ",'%')");
                break;
        }
        ISqlTemplateExpression function = factory.template(functions, Collections.singletonList(right));
        return factory.binary(SqlOperator.LIKE, left, function);
    }

    public static ISqlBinaryExpression startsWith(IConfig config, ISqlExpression left, ISqlExpression right)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> args = Collections.singletonList(right);
        switch (config.getDbType())
        {
            case SQLite:
                functions = Arrays.asList("(", "||'%')");
                break;
            default:
                functions = Arrays.asList("CONCAT(", ",'%')");
                break;
        }
        return factory.binary(SqlOperator.LIKE, left, factory.template(functions, args));
    }

    public static ISqlBinaryExpression endsWith(IConfig config, ISqlExpression left, ISqlExpression right)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> args = Collections.singletonList(right);
        switch (config.getDbType())
        {
            case SQLite:
                functions = Arrays.asList("('%'||", ")");
                break;
            default:
                functions = Arrays.asList("CONCAT('%',", ")");
                break;
        }
        return factory.binary(SqlOperator.LIKE, left, factory.template(functions, args));
    }

    public static ISqlTemplateExpression length(IConfig config, ISqlExpression thiz)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        switch (config.getDbType())
        {
            case SQLServer:
                functions = Arrays.asList("LEN(", ")");
                break;
            case Oracle:
                functions = Arrays.asList("NVL(LENGTH(", "),0)");
                break;
            case SQLite:
            case PostgreSQL:
                functions = Arrays.asList("LENGTH(", ")");
                break;
            case MySQL:
            case H2:
            default:
                functions = Arrays.asList("CHAR_LENGTH(", ")");
        }
        return factory.template(functions, Collections.singletonList(thiz));
    }

    public static ISqlTemplateExpression toUpperCase(IConfig config, ISqlExpression thiz)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("UPPER(", ")");
        return factory.template(functions, Collections.singletonList(thiz));
    }

    public static ISqlTemplateExpression toLowerCase(IConfig config, ISqlExpression thiz)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("LOWER(", ")");
        return factory.template(functions, Collections.singletonList(thiz));
    }

    public static ISqlTemplateExpression concat(IConfig config, ISqlExpression left, ISqlExpression right)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        switch (config.getDbType())
        {
            case SQLite:
                functions = Arrays.asList("(", "||", ")");
                break;
            default:
                functions = Arrays.asList("CONCAT(", ",", ")");
        }
        return factory.template(functions, Arrays.asList(left, right));
    }

    public static ISqlTemplateExpression trim(IConfig config, ISqlExpression thiz)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("TRIM(", ")");
        return factory.template(functions, Collections.singletonList(thiz));
    }

    public static ISqlExpression isEmpty(IConfig config, ISqlExpression thiz)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        switch (config.getDbType())
        {
            case SQLServer:
                return factory.parens(factory.binary(SqlOperator.EQ, factory.template(Arrays.asList("DATALENGTH(", ")"), Collections.singletonList(thiz)), factory.constString("0")));
            default:
                return factory.parens(factory.binary(SqlOperator.EQ, length(config, thiz), factory.constString("0")));
        }
//        if (config.getDbType() == DbType.SQLServer)
//        {
//            return factory.template(Arrays.asList("IIF(", ",1,0)"), Collections.singletonList(parens));
//        }
//        else
//        {
//            return parens;
//        }
    }

    public static ISqlTemplateExpression indexOf(IConfig config, ISqlExpression thisStr, ISqlExpression subStr)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType())
        {
            case SQLServer:
                functions = Arrays.asList("CHARINDEX(", ",", ")");
                sqlExpressions = Arrays.asList(subStr, thisStr);
                break;
            case PostgreSQL:
                functions = Arrays.asList("STRPOS(", ",", ")");
                sqlExpressions = Arrays.asList(thisStr, subStr);
                break;
            default:
                functions = Arrays.asList("INSTR(", ",", ")");
                sqlExpressions = Arrays.asList(thisStr, subStr);
        }
        return factory.template(functions, sqlExpressions);
    }

    public static ISqlTemplateExpression indexOf(IConfig config, ISqlExpression thisStr, ISqlExpression subStr, ISqlExpression fromIndex)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType())
        {
            case SQLServer:
                functions = Arrays.asList("CHARINDEX(", ",", ",", ")");
                sqlExpressions = Arrays.asList(subStr, thisStr, fromIndex);
                break;
            case Oracle:
                functions = Arrays.asList("INSTR(", ",", ",", ")");
                sqlExpressions = Arrays.asList(thisStr, subStr, fromIndex);
                break;
            case SQLite:
                functions = Arrays.asList("(INSTR(SUBSTR(", ",", " + 1),", ") + ", ")");
                sqlExpressions = Arrays.asList(thisStr, fromIndex, subStr, fromIndex);
                break;
            case PostgreSQL:
                functions = Arrays.asList("(STRPOS(SUBSTR(", ",", " + 1),", ") + ", ")");
                sqlExpressions = Arrays.asList(thisStr, fromIndex, subStr, fromIndex);
                break;
            default:
                functions = Arrays.asList("LOCATE(", ",", ",", ")");
                sqlExpressions = Arrays.asList(subStr, thisStr, fromIndex);
        }
        return factory.template(functions, sqlExpressions);
    }

    public static ISqlTemplateExpression replace(IConfig config, ISqlExpression thisStr, ISqlExpression oldStr, ISqlExpression newStr)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("REPLACE(", ",", ",", ")");
        List<ISqlExpression> sqlExpressions = Arrays.asList(thisStr, oldStr, newStr);
        return factory.template(functions, sqlExpressions);
    }

    public static ISqlTemplateExpression substring(IConfig config, ISqlExpression thisStr, ISqlExpression beginIndex)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType())
        {
            case SQLServer:
                functions = Arrays.asList("SUBSTRING(", ",", ",LEN(", ") - (", " - 1))");
                sqlExpressions = Arrays.asList(thisStr, beginIndex, thisStr, beginIndex);
                break;
            default:
                functions = Arrays.asList("SUBSTR(", ",", ")");
                sqlExpressions = Arrays.asList(thisStr, beginIndex);
                break;
        }
        return factory.template(functions, sqlExpressions);
    }

    public static ISqlTemplateExpression substring(IConfig config, ISqlExpression thisStr, ISqlExpression beginIndex, ISqlExpression endIndex)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions = Arrays.asList(thisStr, beginIndex, endIndex);
        switch (config.getDbType())
        {
            case SQLServer:
                functions = Arrays.asList("SUBSTRING(", ",", ",", ")");
                break;
            default:
                functions = Arrays.asList("SUBSTR(", ",", ",", ")");
        }
        return factory.template(functions, sqlExpressions);
    }

    public static ISqlTemplateExpression joinArray(IConfig config, ISqlExpression delimiter, List<ISqlExpression> elements)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>(1 + elements.size());
        switch (config.getDbType())
        {
            case Oracle:
            case SQLite:
                functions.add("(");
                for (int i = 0; i < elements.size(); i++)
                {
                    sqlExpressions.add(elements.get(i));
                    if (i < elements.size() - 1)
                    {
                        functions.add("||");
                        sqlExpressions.add(delimiter);
                        functions.add("||");
                    }
                }
                functions.add(")");
                break;
            default:
                sqlExpressions.add(delimiter);
                sqlExpressions.addAll(elements);
                functions.add("CONCAT_WS(");
                functions.add(",");
                for (int i = 0; i < sqlExpressions.size(); i++)
                {
                    if (i < elements.size() - 1) functions.add(",");
                }
                functions.add(")");
        }
        return factory.template(functions, sqlExpressions);
    }

    public static ISqlTemplateExpression joinList(IConfig config, ISqlExpression delimiter, ISqlExpression elements)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        if (elements instanceof ISqlCollectedValueExpression)
        {
            if (config.getDbType() == DbType.Oracle || config.getDbType() == DbType.SQLite)
            {
                ISqlCollectedValueExpression expression = (ISqlCollectedValueExpression) elements;
                List<Object> collection = new ArrayList<>(expression.getCollection());
                functions = new ArrayList<>(collection.size() * 2);
                sqlExpressions = new ArrayList<>(collection.size() * 2);
                functions.add("(");
                for (int i = 0; i < collection.size(); i++)
                {
                    sqlExpressions.add(factory.value(collection.get(i)));
                    if (i < collection.size() - 1)
                    {
                        functions.add("||");
                        sqlExpressions.add(delimiter);
                        functions.add("||");
                    }
                }
                functions.add(")");
            }
            else
            {
                functions = new ArrayList<>();
                sqlExpressions = Arrays.asList(delimiter, elements);
            }
        }
        else
        {
            throw new SQLinkException("String.join()的第二个参数必须是java中能获取到的");
        }
        switch (config.getDbType())
        {
            case Oracle:
            case SQLite:
                break;
            default:
                functions.add("CONCAT_WS(");
                functions.add(",");
                functions.add(")");
        }
        return factory.template(functions, sqlExpressions);
    }
}
