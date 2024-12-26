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
package org.noear.solon.data.sqlink.core.expression.oracle;

import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.expression.impl.DefaultSqlExpressionFactory;

/**
 * Oracle表达式工厂
 *
 * @author kiryu1223
 * @since 3.0
 */
public class OracleExpressionFactory extends DefaultSqlExpressionFactory {
    @Override
    public ISqlFromExpression from(ISqlTableExpression sqlTable, AsName asName) {
        return new OracleFromExpression(sqlTable, asName);
    }

    @Override
    public ISqlJoinExpression join(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, AsName asName) {
        return new OracleJoinExpression(joinType, joinTable, conditions, asName);
    }

    @Override
    public ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit) {
        return new OracleQueryableExpression(select, from, joins, where, groupBy, having, orderBy, limit);
    }

    @Override
    public ISqlLimitExpression limit() {
        return new OracleLimitExpression();
    }
}
