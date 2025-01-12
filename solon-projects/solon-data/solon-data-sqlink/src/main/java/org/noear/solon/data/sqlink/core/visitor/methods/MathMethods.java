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
 * 数学运算函数
 *
 * @author kiryu1223
 * @since 3.0
 */
public class MathMethods {
    /**
     * 数据库atan2函数
     */
    public static ISqlTemplateExpression atan2(SqLinkConfig config, ISqlExpression arg1, ISqlExpression arg2) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case SQLServer:
                function = Arrays.asList("ATN2(", ",", ")");
                break;
            default:
                function = Arrays.asList("ATAN2(", ",", ")");
        }
        return factory.template(function, Arrays.asList(arg1, arg2));
    }

    /**
     * 数据库ceil函数
     */
    public static ISqlTemplateExpression ceil(SqLinkConfig config, ISqlExpression arg) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case SQLServer:
                function = Arrays.asList("CEILING(", ")");
                break;
            default:
                function = Arrays.asList("CEIL(", ")");
        }
        return factory.template(function, Collections.singletonList(arg));
    }

    /**
     * 数据库degrees函数
     */
    public static ISqlTemplateExpression toDegrees(SqLinkConfig config, ISqlExpression arg) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case Oracle:
                function = Arrays.asList("(", " * 180 / " + Math.PI + ")");
                break;
            default:
                function = Arrays.asList("DEGREES(", ")");
        }
        return factory.template(function, Collections.singletonList(arg));
    }

    /**
     * 数据库radians函数
     */
    public static ISqlTemplateExpression toRadians(SqLinkConfig config, ISqlExpression arg) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case Oracle:
                function = Arrays.asList("(", " * " + Math.PI + " / 180)");
                break;
            default:
                function = Arrays.asList("RADIANS(", ")");
        }
        return factory.template(function, Collections.singletonList(arg));
    }

    /**
     * 数据库log函数
     */
    public static ISqlTemplateExpression log(SqLinkConfig config, ISqlExpression arg) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case SQLServer:
                function = Arrays.asList("LOG(", ")");
                break;
            default:
                function = Arrays.asList("LN(", ")");
        }
        return factory.template(function, Collections.singletonList(arg));
    }

    /**
     * 数据库log10函数
     */
    public static ISqlTemplateExpression log10(SqLinkConfig config, ISqlExpression arg) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case SQLServer:
                function = Arrays.asList("LOG(", ",10)");
                break;
            case Oracle:
                function = Arrays.asList("LOG(10,", ")");
                break;
            default:
                function = Arrays.asList("LOG10(", ")");
        }
        return factory.template(function, Collections.singletonList(arg));
    }

    /**
     * 数据库random函数
     */
    public static ISqlTemplateExpression random(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case Oracle:
                function = Collections.singletonList("DBMS_RANDOM.VALUE");
                break;
            case SQLite:
                function = Collections.singletonList("ABS(RANDOM() / 10000000000000000000.0)");
                break;
            case PostgreSQL:
                function = Collections.singletonList("RANDOM()");
                break;
            default:
                function = Collections.singletonList("RAND()");
        }
        return factory.template(function, Collections.emptyList());
    }

    /**
     * 数据库round函数
     */
    public static ISqlTemplateExpression round(SqLinkConfig config, ISqlExpression arg) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        List<String> function;
        switch (config.getDbType()) {
            case SQLServer:
                function = Arrays.asList("ROUND(", ",0)");
                break;
            default:
                function = Arrays.asList("ROUND(", ")");
        }
        return factory.template(function, Collections.singletonList(arg));
    }
}
