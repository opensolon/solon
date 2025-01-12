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

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTemplateExpression;
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 聚合函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public class AggregateMethods {

    /**
     * COUNT
     */
    public static ISqlTemplateExpression count(SqLinkConfig config, ISqlExpression expression) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlTemplateExpression templateExpression;
        if (expression == null) {
            templateExpression = factory.template(Collections.singletonList("COUNT(*)"), Collections.emptyList());
        }
        else {
            templateExpression = factory.template(Arrays.asList("COUNT(", ")"), Collections.singletonList(expression));
        }
        return templateExpression;
    }

    /**
     * COUNT
     */
    public static ISqlTemplateExpression count(SqLinkConfig config) {
        return count(config, null);
    }

    /**
     * SUM
     */
    public static ISqlTemplateExpression sum(SqLinkConfig config, ISqlExpression expression) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.template(Arrays.asList("SUM(", ")"), Collections.singletonList(expression));
    }

    /**
     * AVG
     */
    public static ISqlTemplateExpression avg(SqLinkConfig config, ISqlExpression expression) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.template(Arrays.asList("AVG(", ")"), Collections.singletonList(expression));
    }

    /**
     * MAX
     */
    public static ISqlTemplateExpression max(SqLinkConfig config, ISqlExpression expression) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.template(Arrays.asList("MAX(", ")"), Collections.singletonList(expression));
    }

    /**
     * MIN
     */
    public static ISqlTemplateExpression min(SqLinkConfig config, ISqlExpression expression) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.template(Arrays.asList("MIN(", ")"), Collections.singletonList(expression));
    }

    /**
     * GROUP_CONCAT
     */
    public static ISqlTemplateExpression groupConcat(SqLinkConfig config, List<ISqlExpression> expressions) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> strings;
        List<ISqlExpression> args;
        //无分隔符
        if (expressions.size() == 1) {
            ISqlExpression property = expressions.get(0);
            switch (config.getDbType()) {
                case SQLServer:
                    strings = Arrays.asList("STRING_AGG(", ",',')");
                    args = Collections.singletonList(property);
                    break;
                case PostgreSQL:
                    strings = Arrays.asList("STRING_AGG(", "::TEXT,',')");
                    args = Collections.singletonList(property);
                    break;
                case Oracle:
                    strings = Arrays.asList("LISTAGG(", ") WITHIN GROUP (ORDER BY ", ")");
                    args = Arrays.asList(property, property);
                    break;
                default:
                    strings = Arrays.asList("GROUP_CONCAT(", ")");
                    args = Collections.singletonList(property);
            }
        }
        //有分隔符
        else {
            ISqlExpression delimiter = expressions.get(0);
            ISqlExpression property = expressions.get(1);
            switch (config.getDbType()) {
                case Oracle:
                    strings = Arrays.asList("LISTAGG(", ",", ") WITHIN GROUP (ORDER BY ", ")");
                    args = Arrays.asList(property, delimiter, property);
                    break;
                case SQLServer:
                    strings = Arrays.asList("STRING_AGG(", ",", ")");
                    args = Arrays.asList(property, delimiter);
                case PostgreSQL:
                    strings = Arrays.asList("STRING_AGG(", "::TEXT,", ")");
                    args = Arrays.asList(property, delimiter);
                case SQLite:
                    strings = Arrays.asList("GROUP_CONCAT(", ",", ")");
                    args = Arrays.asList(property, delimiter);
                default:
                    strings = Arrays.asList("GROUP_CONCAT(", " SEPARATOR ", ")");
                    args = Arrays.asList(property, delimiter);
            }
        }
        return factory.template(strings, args);
    }
}

