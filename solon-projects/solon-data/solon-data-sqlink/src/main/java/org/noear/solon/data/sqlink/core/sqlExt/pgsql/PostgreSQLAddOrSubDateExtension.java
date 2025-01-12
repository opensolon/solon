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
package org.noear.solon.data.sqlink.core.sqlExt.pgsql;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlSingleValueExpression;
import org.noear.solon.data.sqlink.base.sqlExt.BaseSqlExtension;
import org.noear.solon.data.sqlink.core.exception.SqLinkIntervalException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL时间运算函数扩展
 *
 * @author kiryu1223
 * @since 3.0
 */
public class PostgreSQLAddOrSubDateExtension extends BaseSqlExtension {
    @Override
    public ISqlExpression parse(SqLinkConfig config, Method method, List<ISqlExpression> args) {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        boolean isPlus = method.getName().equals("addDate");
        if (isPlus) {
            templates.add("DATE_ADD(");
        }
        else {
            templates.add("DATE_SUBTRACT(");
        }
        sqlExpressions.add(args.get(0));
        if (method.getParameterCount() == 2) {
            ISqlExpression num = args.get(1);
            if (num instanceof ISqlSingleValueExpression) {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                templates.add(",INTERVAL '" + valueExpression.getValue() + "' DAY)");
            }
            else {
                throw new SqLinkIntervalException(DbType.PostgreSQL);
            }
        }
        else {
            ISqlExpression num = args.get(2);
            if (num instanceof ISqlSingleValueExpression) {
                ISqlSingleValueExpression valueExpression = (ISqlSingleValueExpression) num;
                templates.add(",INTERVAL '" + valueExpression.getValue() + "' ");
                sqlExpressions.add(args.get(1));
                templates.add(")");
            }
            else {
                throw new SqLinkIntervalException(DbType.PostgreSQL);
            }
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
