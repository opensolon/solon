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
package org.noear.solon.data.sqlink.core.sqlExt.pgsql;

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.base.sqlExt.SqlTimeUnit;
import org.noear.solon.data.sqlink.core.exception.SQLinkException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class PostgreSQLDateTimeDiffExtension extends BaseSqlExtension {
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args) {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        ISqlExpression unit = args.get(0);
        ISqlExpression from = args.get(1);
        ISqlExpression to = args.get(2);
        Class<?>[] parameterTypes = sqlFunc.getParameterTypes();
        boolean isToIsString = parameterTypes[2] == String.class;
        boolean isFromIsString = parameterTypes[1] == String.class;
        String toString = isToIsString ? "::TIMESTAMP" : "";
        String fromString = isFromIsString ? "::TIMESTAMP" : "";
        if (unit instanceof ISqlSingleValueExpression) {
            ISqlSingleValueExpression sqlSingleValueExpression = (ISqlSingleValueExpression) unit;
            SqlTimeUnit timeUnit = (SqlTimeUnit) sqlSingleValueExpression.getValue();
            switch (timeUnit) {
                case YEAR:
                    templates.add("EXTRACT(YEAR FROM AGE(");
                    sqlExpressions.add(to);
                    templates.add(toString + ",");
                    sqlExpressions.add(from);
                    templates.add(fromString + "))::INT8");
                    break;
                case MONTH:
                    templates.add("(EXTRACT(YEAR FROM AGE(");
                    sqlExpressions.add(to);
                    templates.add(toString + ",");
                    sqlExpressions.add(from);
                    templates.add(fromString + ")) * 12 + EXTRACT(MONTH FROM AGE(");
                    sqlExpressions.add(to);
                    templates.add(toString + ",");
                    sqlExpressions.add(from);
                    templates.add(fromString + ")))::INT8");
                    break;
                case WEEK:
                    templates.add("(EXTRACT(DAY FROM (");
                    sqlExpressions.add(to);
                    templates.add(toString + " - ");
                    sqlExpressions.add(from);
                    templates.add(fromString + ")) / 7)::INT8");
                    break;
                case DAY:
                    templates.add("EXTRACT(DAY FROM (");
                    sqlExpressions.add(to);
                    templates.add(toString + " - ");
                    sqlExpressions.add(from);
                    templates.add(fromString + "))::INT8");
                    break;
                case HOUR:
                    templates.add("(EXTRACT(EPOCH FROM ");
                    sqlExpressions.add(to);
                    templates.add(toString + " - ");
                    sqlExpressions.add(from);
                    templates.add(fromString + ") / 3600)::INT8");
                    break;
                case MINUTE:
                    templates.add("(EXTRACT(EPOCH FROM ");
                    sqlExpressions.add(to);
                    templates.add(toString + " - ");
                    sqlExpressions.add(from);
                    templates.add(fromString + ") / 60)::INT8");
                    break;
                case SECOND:
                    templates.add("EXTRACT(EPOCH FROM ");
                    sqlExpressions.add(to);
                    templates.add(toString + " - ");
                    sqlExpressions.add(from);
                    templates.add(fromString + ")::INT8");
                    break;
                case MILLISECOND:
                    templates.add("(EXTRACT(EPOCH FROM ");
                    sqlExpressions.add(to);
                    templates.add(toString + " - ");
                    sqlExpressions.add(from);
                    templates.add(fromString + ") * 1000)::INT8");
                    break;
            }
        }
        else {
            throw new SQLinkException("SqlTimeUnit必须为可求值的");
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
