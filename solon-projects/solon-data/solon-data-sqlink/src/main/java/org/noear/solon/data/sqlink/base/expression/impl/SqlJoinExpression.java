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
package org.noear.solon.data.sqlink.base.expression.impl;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.List;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class SqlJoinExpression implements ISqlJoinExpression {
    protected final JoinType joinType;
    protected final ISqlTableExpression joinTable;
    protected final ISqlExpression conditions;
    protected final AsName asName;

    protected SqlJoinExpression(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, AsName asName) {
        this.joinType = joinType;
        this.joinTable = joinTable;
        this.conditions = conditions;
        this.asName = asName;
    }

    @Override
    public JoinType getJoinType() {
        return joinType;
    }

    @Override
    public ISqlTableExpression getJoinTable() {
        return joinTable;
    }

    @Override
    public ISqlExpression getConditions() {
        return conditions;
    }

    @Override
    public AsName getAsName() {
        return asName;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        StringBuilder builder = new StringBuilder();
        SqLinkDialect disambiguation = config.getDisambiguation();
        builder.append(joinType.getJoin());
        builder.append(" ");
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
            builder.append(" AS ").append(disambiguation.disambiguation(getAsName().getName()));
        }
        builder.append(" ON ");
        builder.append(conditions.getSqlAndValue(config, values));
        return builder.toString();
    }
}
