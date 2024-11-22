/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.solon.data.sqlink.base.expression.SqlExpressionFactory;
import org.noear.solon.data.sqlink.base.expression.SqlOperator;

import java.util.Arrays;
import java.util.List;

/**
 * 逻辑运算
 *
 * @author kiryu1223
 * @since 3.0
 */
public class LogicExpression {
    /**
     * if表达式
     */
    public static ISqlExpression IfExpression(SqLinkConfig config, ISqlExpression cond, ISqlExpression truePart, ISqlExpression falsePart) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        List<ISqlExpression> args = Arrays.asList(cond, truePart, falsePart);
        switch (config.getDbType()) {
            case SQLServer:
            case SQLite:
                function = Arrays.asList("IIF(", ",", ",", ")");
                break;
            case Oracle:
            case PostgreSQL:
                function = Arrays.asList("(CASE WHEN ", " THEN ", " ELSE ", " END)");
                break;
            default:
                function = Arrays.asList("IF(", ",", ",", ")");
        }
        return factory.template(function, args);
    }

    /**
     * 是否为空表达式
     */
    public static ISqlExpression notNullExpression(SqLinkConfig config, ISqlExpression expression) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.binary(SqlOperator.IS, expression, factory.unary(SqlOperator.NOT,factory.AnyValue(null)));
    }

}
