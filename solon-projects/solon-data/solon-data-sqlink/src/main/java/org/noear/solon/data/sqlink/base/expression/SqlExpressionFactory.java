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
package org.noear.solon.data.sqlink.base.expression;

import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * sql表达式工厂
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface SqlExpressionFactory
{
    ISqlAsExpression as(ISqlExpression expression, String asName);

    default ISqlColumnExpression column(PropertyMetaData propertyMetaData)
    {
        return column(propertyMetaData, 0);
    }

    ISqlColumnExpression column(PropertyMetaData propertyMetaData, int tableIndex);

    ISqlConditionsExpression condition();

    default ISqlFromExpression from(ISqlTableExpression sqlTable)
    {
        return from(sqlTable, 0);
    }

    ISqlFromExpression from(ISqlTableExpression sqlTable, int index);

    ISqlGroupByExpression groupBy();

    default ISqlGroupByExpression groupBy(LinkedHashMap<String, ISqlExpression> columns)
    {
        ISqlGroupByExpression groupByExpression = groupBy();
        groupByExpression.setColumns(columns);
        return groupByExpression;
    }

    ISqlHavingExpression having();

    ISqlJoinExpression join(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, int index);

    ISqlJoinsExpression Joins();

    ISqlLimitExpression limit();

    default ISqlLimitExpression limit(long offset, long rows)
    {
        ISqlLimitExpression limit = limit();
        limit.setOffset(offset);
        limit.setRows(rows);
        return limit;
    }

    ISqlOrderByExpression orderBy();

    default ISqlOrderExpression order(ISqlExpression expression)
    {
        return order(expression, true);
    }

    ISqlOrderExpression order(ISqlExpression expression, boolean asc);

    default ISqlQueryableExpression queryable(Class<?> target)
    {
        return queryable(from(table(target), 0));
    }

    default ISqlQueryableExpression queryable(Class<?> target, int offset)
    {
        return queryable(from(table(target), offset));
    }

    default ISqlQueryableExpression queryable(ISqlFromExpression from)
    {
        return queryable(select(from.getSqlTableExpression().getTableClass()), from, Joins(), where(), groupBy(), having(), orderBy(), limit());
    }

    default ISqlQueryableExpression queryable(ISqlTableExpression table)
    {
        return queryable(from(table));
    }

    ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit);

    ISqlRealTableExpression table(Class<?> tableClass);

    default ISqlSelectExpression select(Class<?> target)
    {
        return select(getColumnByClass(target), target, false, false);
    }

    default ISqlSelectExpression select(List<ISqlExpression> column, Class<?> target)
    {
        return select(column, target, false, false);
    }

    ISqlSelectExpression select(List<ISqlExpression> column, Class<?> target, boolean isSingle, boolean isDistinct);

    default ISqlWhereExpression where()
    {
        return where(condition());
    }

    ISqlWhereExpression where(ISqlConditionsExpression conditions);

    ISqlSetExpression set(ISqlColumnExpression column, ISqlExpression value);

    default ISqlValueExpression AnyValue(Object value)
    {
        if (value instanceof Collection<?>)
        {
            Collection<Object> objects = (Collection<Object>) value;
            return value(objects);
        }
        else
        {
            return value(value);
        }
    }

    ISqlSingleValueExpression value(Object value);

    ISqlCollectedValueExpression value(Collection<Object> value);

    ISqlTemplateExpression template(List<String> templates, List<? extends ISqlExpression> expressions);

    ISqlBinaryExpression binary(SqlOperator operator, ISqlExpression left, ISqlExpression right);

    ISqlUnaryExpression unary(SqlOperator operator, ISqlExpression expression);

    ISqlParensExpression parens(ISqlExpression expression);

    ISqlConstStringExpression constString(String s);

    ISqlSetsExpression sets();

    ISqlTypeExpression type(Class<?> c);

    default List<ISqlExpression> getColumnByClass(Class<?> target)
    {
        MetaData metaData = MetaDataCache.getMetaData(target);
        List<PropertyMetaData> property = metaData.getNotIgnorePropertys();
        List<ISqlExpression> columns = new ArrayList<>(property.size());
        for (PropertyMetaData data : property)
        {
            columns.add(column(data, 0));
        }
        return columns;
    }
}
