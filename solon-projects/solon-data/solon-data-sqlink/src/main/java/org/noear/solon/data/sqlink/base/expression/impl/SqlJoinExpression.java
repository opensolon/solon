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
    protected final int index;

    protected SqlJoinExpression(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, int index) {
        this.joinType = joinType;
        this.joinTable = joinTable;
        this.conditions = conditions;
        this.index = index;
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
    public int getIndex() {
        return index;
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        String t = "t" + getIndex();
        return getJoinType().getJoin() + " " + (getJoinTable() instanceof ISqlRealTableExpression ? getJoinTable().getSqlAndValue(config, values) : "(" + getJoinTable().getSqlAndValue(config, values) + ")") + " AS " + config.getDisambiguation().disambiguation(t) + " ON " + getConditions().getSqlAndValue(config, values);
    }
}
