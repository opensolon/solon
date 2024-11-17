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

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.ISqlExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlRealTableExpression;
import org.noear.solon.data.sqlink.base.expression.ISqlTableExpression;
import org.noear.solon.data.sqlink.base.expression.JoinType;
import org.noear.solon.data.sqlink.base.expression.impl.SqlJoinExpression;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * Oracle Join 表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public class OracleJoinExpression extends SqlJoinExpression {
    protected OracleJoinExpression(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, String asName) {
        super(joinType, joinTable, conditions, asName);
    }

    // oracle下表的别名不能加AS
    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        return joinType.getJoin() + " " + (joinTable instanceof ISqlRealTableExpression ? joinTable.getSqlAndValue(config, values) : "(" + joinTable.getSqlAndValue(config, values) + ")") + " " + config.getDisambiguation().disambiguation(asName) + " ON " + conditions.getSqlAndValue(config, values);
    }
}
