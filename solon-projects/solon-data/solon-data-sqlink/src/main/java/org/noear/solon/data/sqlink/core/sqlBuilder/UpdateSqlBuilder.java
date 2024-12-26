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
package org.noear.solon.data.sqlink.core.sqlBuilder;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.SqLinkDialect;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;

import java.util.ArrayList;
import java.util.List;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.doGetAsName;
import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.getFirst;

/**
 * 更新语句构造器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class UpdateSqlBuilder implements ISqlBuilder {
    private final SqLinkConfig config;
    private final SqlExpressionFactory factory;
    private final ISqlUpdateExpression update;

    public UpdateSqlBuilder(SqLinkConfig config, ISqlUpdateExpression update) {
        this.config = config;
        this.update = update;
        this.factory = config.getSqlExpressionFactory();
    }

    /**
     * 添加关联表
     *
     * @param joinType 关联类型
     * @param table    关联表
     * @param on       关联条件
     */
    public void addJoin(JoinType joinType, ISqlTableExpression table, ISqlExpression on) {
        String first = getFirst(table.getMainTableClass());
        AsName asName = doGetAsName(first,update.getFrom(),update.getJoins());
        ISqlJoinExpression join = factory.join(
                joinType,
                table,
                on,
                asName
        );
        update.addJoin(join);
    }

    /**
     * 添加需要更新的列
     */
    public void addSet(ISqlSetExpression set) {
        update.addSet(set);
    }

    /**
     * 添加条件
     */
    public void addWhere(ISqlExpression where) {
        update.addWhere(where);
    }

    /**
     * 是否有条件
     */
    public boolean hasWhere() {
        return !update.getWhere().isEmpty();
    }

    public boolean hasSet() {
        return !update.getSets().isEmpty();
    }

    @Override
    public String getSql() {
        return getSqlAndValue(null);
    }

    @Override
    public String getSqlAndValue(List<SqlValue> sqlValues) {
        return update.getSqlAndValue(config, sqlValues);
    }

    public SqLinkConfig getConfig() {
        return config;
    }

    public ISqlUpdateExpression getUpdate() {
        return update;
    }
}
