/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.data.sqlink.core.visitor.methods;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 字符串函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public class StringMethods {

    /**
     * 数据库LIKE运算
     */
    public static ISqlBinaryExpression contains(SqLinkConfig config, ISqlExpression left, ISqlExpression right) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        switch (config.getDbType()) {
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


    /**
     * 数据库LIKE左匹配运算
     */
    public static ISqlBinaryExpression startsWith(SqLinkConfig config, ISqlExpression left, ISqlExpression right) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> args = Collections.singletonList(right);
        switch (config.getDbType()) {
            case SQLite:
                functions = Arrays.asList("(", "||'%')");
                break;
            default:
                functions = Arrays.asList("CONCAT(", ",'%')");
                break;
        }
        return factory.binary(SqlOperator.LIKE, left, factory.template(functions, args));
    }

    /**
     * 数据库LIKE右匹配运算
     */
    public static ISqlBinaryExpression endsWith(SqLinkConfig config, ISqlExpression left, ISqlExpression right) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> args = Collections.singletonList(right);
        switch (config.getDbType()) {
            case SQLite:
                functions = Arrays.asList("('%'||", ")");
                break;
            default:
                functions = Arrays.asList("CONCAT('%',", ")");
                break;
        }
        return factory.binary(SqlOperator.LIKE, left, factory.template(functions, args));
    }

    /**
     * 数据库字符串长度函数
     */
    public static ISqlTemplateExpression length(SqLinkConfig config, ISqlExpression thiz) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        switch (config.getDbType()) {
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

    /**
     * 数据库字符串转大写函数
     */
    public static ISqlTemplateExpression toUpperCase(SqLinkConfig config, ISqlExpression thiz) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("UPPER(", ")");
        return factory.template(functions, Collections.singletonList(thiz));
    }

    /**
     * 数据库字符串转小写函数
     */
    public static ISqlTemplateExpression toLowerCase(SqLinkConfig config, ISqlExpression thiz) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("LOWER(", ")");
        return factory.template(functions, Collections.singletonList(thiz));
    }

    /**
     * 数据库字符串拼接函数
     */
    public static ISqlTemplateExpression concat(SqLinkConfig config, ISqlExpression left, ISqlExpression right) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        switch (config.getDbType()) {
            case SQLite:
                functions = Arrays.asList("(", "||", ")");
                break;
            default:
                functions = Arrays.asList("CONCAT(", ",", ")");
        }
        return factory.template(functions, Arrays.asList(left, right));
    }

    /**
     * 数据库字符串左右去空格函数
     */
    public static ISqlTemplateExpression trim(SqLinkConfig config, ISqlExpression thiz) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("TRIM(", ")");
        return factory.template(functions, Collections.singletonList(thiz));
    }

    /**
     * 数据库判断字符串是否为空表达式
     */
    public static ISqlExpression isEmpty(SqLinkConfig config, ISqlExpression thiz) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        switch (config.getDbType()) {
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

    /**
     * 数据库字符串查找索引函数
     */
    public static ISqlTemplateExpression indexOf(SqLinkConfig config, ISqlExpression thisStr, ISqlExpression subStr) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType()) {
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

    /**
     * 数据库字符串查找索引函数
     */
    public static ISqlTemplateExpression indexOf(SqLinkConfig config, ISqlExpression thisStr, ISqlExpression subStr, ISqlExpression fromIndex) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType()) {
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

    /**
     * 数据库字符串替换函数
     */
    public static ISqlTemplateExpression replace(SqLinkConfig config, ISqlExpression thisStr, ISqlExpression oldStr, ISqlExpression newStr) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = Arrays.asList("REPLACE(", ",", ",", ")");
        List<ISqlExpression> sqlExpressions = Arrays.asList(thisStr, oldStr, newStr);
        return factory.template(functions, sqlExpressions);
    }

    /**
     * 数据库字符串截取函数
     */
    public static ISqlTemplateExpression substring(SqLinkConfig config, ISqlExpression thisStr, ISqlExpression beginIndex) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        switch (config.getDbType()) {
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

    /**
     * 数据库字符串截取函数
     */
    public static ISqlTemplateExpression substring(SqLinkConfig config, ISqlExpression thisStr, ISqlExpression beginIndex, ISqlExpression endIndex) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions = Arrays.asList(thisStr, beginIndex, endIndex);
        switch (config.getDbType()) {
            case SQLServer:
                functions = Arrays.asList("SUBSTRING(", ",", ",", ")");
                break;
            default:
                functions = Arrays.asList("SUBSTR(", ",", ",", ")");
        }
        return factory.template(functions, sqlExpressions);
    }

    /**
     * 数据库字符串连接函数
     */
    public static ISqlTemplateExpression joinArray(SqLinkConfig config, ISqlExpression delimiter, List<ISqlExpression> elements) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>(1 + elements.size());
        switch (config.getDbType()) {
            case Oracle:
            case SQLite:
                functions.add("(");
                for (int i = 0; i < elements.size(); i++) {
                    sqlExpressions.add(elements.get(i));
                    if (i < elements.size() - 1) {
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
                for (int i = 0; i < sqlExpressions.size(); i++) {
                    if (i < elements.size() - 1) functions.add(",");
                }
                functions.add(")");
        }
        return factory.template(functions, sqlExpressions);
    }

    /**
     * 数据库字符串连接函数
     */
    public static ISqlTemplateExpression joinList(SqLinkConfig config, ISqlExpression delimiter, ISqlExpression elements) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> functions;
        List<ISqlExpression> sqlExpressions;
        if (elements instanceof ISqlCollectedValueExpression) {
            if (config.getDbType() == DbType.Oracle || config.getDbType() == DbType.SQLite) {
                ISqlCollectedValueExpression expression = (ISqlCollectedValueExpression) elements;
                List<Object> collection = new ArrayList<>(expression.getCollection());
                functions = new ArrayList<>(collection.size() * 2);
                sqlExpressions = new ArrayList<>(collection.size() * 2);
                functions.add("(");
                for (int i = 0; i < collection.size(); i++) {
                    sqlExpressions.add(factory.value(collection.get(i)));
                    if (i < collection.size() - 1) {
                        functions.add("||");
                        sqlExpressions.add(delimiter);
                        functions.add("||");
                    }
                }
                functions.add(")");
            }
            else {
                functions = new ArrayList<>();
                sqlExpressions = Arrays.asList(delimiter, elements);
            }
        }
        else {
            throw new SqLinkException("String.join()的第二个参数必须是java中能获取到的");
        }
        switch (config.getDbType()) {
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
