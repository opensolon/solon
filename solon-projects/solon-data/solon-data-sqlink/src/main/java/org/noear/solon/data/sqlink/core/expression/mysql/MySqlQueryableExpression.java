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
package org.noear.solon.data.sqlink.core.expression.mysql;

import org.noear.solon.data.sqlink.base.DbType;
import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.expression.impl.SqlQueryableExpression;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.core.exception.SqLinkLimitNotFoundOrderByException;

import java.util.ArrayList;
import java.util.List;

/**
 * MySql查询表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public class MySqlQueryableExpression extends SqlQueryableExpression {
    public MySqlQueryableExpression(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit, ISqlUnionsExpression union) {
        super(select, from, joins, where, groupBy, having, orderBy, limit, union);
    }

    @Override
    public String getSqlAndValue(SqLinkConfig config, List<SqlValue> values) {
        if (!isChanged && from.getSqlTableExpression() instanceof ISqlQueryableExpression) {
            return from.getSqlTableExpression().getSqlAndValue(config, values);
        }
        else {
            List<String> strings = new ArrayList<>();
            String unionsSqlAndValue = unions.getSqlAndValue(config, values);
            if (!unionsSqlAndValue.isEmpty()) {
                strings.add(unionsSqlAndValue);
                strings.add("(");
            }
            tryWith(config, strings, values);
            strings.add(getSelect().getSqlAndValue(config, values));
            String fromSqlAndValue = getFrom().getSqlAndValue(config, values);
            if (!fromSqlAndValue.isEmpty()) strings.add(fromSqlAndValue);
            String joinsSqlAndValue = getJoins().getSqlAndValue(config, values);
            if (!joinsSqlAndValue.isEmpty()) strings.add(joinsSqlAndValue);
            String whereSqlAndValue = getWhere().getSqlAndValue(config, values);
            if (!whereSqlAndValue.isEmpty()) strings.add(whereSqlAndValue);
            String groupBySqlAndValue = getGroupBy().getSqlAndValue(config, values);
            if (!groupBySqlAndValue.isEmpty()) strings.add(groupBySqlAndValue);
            String havingSqlAndValue = getHaving().getSqlAndValue(config, values);
            if (!havingSqlAndValue.isEmpty()) strings.add(havingSqlAndValue);
            String orderBySqlAndValue = getOrderBy().getSqlAndValue(config, values);
            if (!orderBySqlAndValue.isEmpty()) strings.add(orderBySqlAndValue);
            if (!getFrom().isEmptyTable()) {
                String limitSqlAndValue = getLimit().getSqlAndValue(config, values);
                if (!limitSqlAndValue.isEmpty()) strings.add(limitSqlAndValue);
            }
            if (!unionsSqlAndValue.isEmpty()) {
                strings.add(")");
            }
            return String.join(" ", strings);
        }
    }
}
