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
package org.noear.solon.data.sqlink.core.sqlExt.sqlite;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.base.sqlExt.SqlTimeUnit;
import org.noear.solon.data.sqlink.core.exception.SqLinkException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Sqlite时间运算函数扩展
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqliteDateTimeDiffExtension extends BaseSqlExtension {
    @Override
    public ISqlExpression parse(SqLinkConfig config, Method method, List<ISqlExpression> args) {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        ISqlExpression unit = args.get(0);
        ISqlExpression from = args.get(1);
        ISqlExpression to = args.get(2);
        if (unit instanceof ISqlSingleValueExpression) {
            ISqlSingleValueExpression sqlSingleValueExpression = (ISqlSingleValueExpression) unit;
            SqlTimeUnit timeUnit = (SqlTimeUnit) sqlSingleValueExpression.getValue();
            switch (timeUnit) {
                case YEAR:
                    templates.add("(STRFTIME('%Y',");
                    sqlExpressions.add(to);
                    templates.add(") - STRFTIME('%Y',");
                    sqlExpressions.add(from);
                    templates.add("))");
                    break;
                case MONTH:
                    templates.add("((STRFTIME('%Y',");
                    sqlExpressions.add(to);
                    templates.add(") - STRFTIME('%Y',");
                    sqlExpressions.add(from);
                    templates.add(")) * 12 + STRFTIME('%m',");
                    sqlExpressions.add(to);
                    templates.add(") - STRFTIME('%m',");
                    sqlExpressions.add(from);
                    templates.add("))");
                    break;
                case WEEK:
                    templates.add("((JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add(")) / 7)");
                    break;
                case DAY:
                    templates.add("(JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add("))");
                    break;
                case HOUR:
                    templates.add("((JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add(")) * 24)");
                    break;
                case MINUTE:
                    templates.add("((JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add(")) * 24 * 60)");
                    break;
                case SECOND:
                    templates.add("((JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add(")) * 24 * 60 * 60)");
                    break;
                case MILLISECOND:
                    templates.add("((JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add(")) * 24 * 60 * 60 * 1000)");
                    break;
                case MICROSECOND:
                    templates.add("((JULIANDAY(");
                    sqlExpressions.add(to);
                    templates.add(") - JULIANDAY(");
                    sqlExpressions.add(from);
                    templates.add(")) * 24 * 60 * 60 * 1000 * 1000)");
                    break;
            }
        }
        else {
            throw new SqLinkException("SqlTimeUnit必须为可求值的");
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
