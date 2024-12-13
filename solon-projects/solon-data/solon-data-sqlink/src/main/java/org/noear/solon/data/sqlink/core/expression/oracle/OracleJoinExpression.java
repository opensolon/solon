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
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.*;
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
        StringBuilder builder = new StringBuilder();
        SqLinkDialect disambiguation = config.getDisambiguation();
        builder.append(joinType.getJoin()).append(" ");
        if (joinTable instanceof ISqlRealTableExpression) {
            builder.append(joinTable.getSqlAndValue(config, values));
        }
        else if (joinTable instanceof ISqlWithExpression) {
            ISqlWithExpression table = (ISqlWithExpression) joinTable;
            builder.append(disambiguation.disambiguationTableName(table.withTableName()));
        }
        else {
            builder.append("(").append(joinTable.getSqlAndValue(config, values)).append(")");
        }
        if (getAsName() != null) {
            builder.append(" ").append(disambiguation.disambiguation(getAsName())).append(" ");
        }
        builder.append(" ON ");
        builder.append(conditions.getSqlAndValue(config, values));
        return builder.toString();
    }
}
