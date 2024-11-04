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
package org.noear.solon.data.sqlink.core.sqlExt.mysql;

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
public class MySqlDateTimeDiffExtension extends BaseSqlExtension {
    @Override
    public ISqlExpression parse(IConfig config, Method sqlFunc, List<ISqlExpression> args) {
        List<String> templates = new ArrayList<>();
        List<ISqlExpression> sqlExpressions = new ArrayList<>();
        ISqlExpression expression = args.get(0);
        if (expression instanceof ISqlSingleValueExpression) {
            ISqlSingleValueExpression sqlSingleValueExpression = (ISqlSingleValueExpression) expression;
            SqlTimeUnit timeUnit = (SqlTimeUnit) sqlSingleValueExpression.getValue();
            if (timeUnit == SqlTimeUnit.MILLISECOND) {
                templates.add("(TIMESTAMPDIFF(MICROSECOND,");
                sqlExpressions.add(args.get(1));
                templates.add(",");
                sqlExpressions.add(args.get(2));
                templates.add(") / 1000)");
            }
            else {
                templates.add("TIMESTAMPDIFF(");
                sqlExpressions.add(expression);
                templates.add(",");
                sqlExpressions.add(args.get(1));
                templates.add(",");
                sqlExpressions.add(args.get(2));
                templates.add(")");
            }
        }
        else {
            throw new SQLinkException("SqlTimeUnit必须为可求值的");
        }
        return config.getSqlExpressionFactory().template(templates, sqlExpressions);
    }
}
