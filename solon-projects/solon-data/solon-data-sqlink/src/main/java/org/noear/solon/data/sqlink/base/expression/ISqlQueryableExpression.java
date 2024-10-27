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

import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;
import org.noear.solon.data.sqlink.base.metaData.PropertyMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询语句表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlQueryableExpression extends ISqlTableExpression
{
    @Override
    default ISqlQueryableExpression copy(IConfig config)
    {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        return factory.queryable(getSelect().copy(config), getFrom().copy(config), getJoins().copy(config), getWhere().copy(config), getGroupBy().copy(config), getHaving().copy(config), getOrderBy().copy(config), getLimit().copy(config));
    }

    @Override
    Class<?> getTableClass();

    void addWhere(ISqlExpression cond);

    void addJoin(ISqlJoinExpression join);

    void setGroup(ISqlGroupByExpression group);

    void addHaving(ISqlExpression cond);

    void addOrder(ISqlOrderExpression order);

    void setSelect(ISqlSelectExpression newSelect);

    void setLimit(long offset, long rows);

    void setDistinct(boolean distinct);

    ISqlFromExpression getFrom();

    int getOrderedCount();

    ISqlWhereExpression getWhere();

    ISqlGroupByExpression getGroupBy();

    ISqlJoinsExpression getJoins();

    ISqlSelectExpression getSelect();

    ISqlOrderByExpression getOrderBy();

    ISqlLimitExpression getLimit();

    ISqlHavingExpression getHaving();

    List<Class<?>> getOrderedClass();

    default List<PropertyMetaData> getMappingData(IConfig config)
    {
        List<Class<?>> orderedClass = getOrderedClass();
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        Class<?> target = getSelect().getTarget();
        MetaData metaData = MetaDataCache.getMetaData(target);
        if (orderedClass.contains(target))
        {
            return metaData.getNotIgnorePropertys();
        }
        else
        {
            List<PropertyMetaData> propertyMetaDataList = new ArrayList<>();
            for (PropertyMetaData sel : metaData.getNotIgnorePropertys())
            {
                GOTO:
                for (MetaData data : MetaDataCache.getMetaData(getOrderedClass()))
                {
                    for (PropertyMetaData noi : data.getNotIgnorePropertys())
                    {
                        if (noi.getColumn().equals(sel.getColumn()) && noi.getType().equals(sel.getType()))
                        {
                            propertyMetaDataList.add(sel);
                            break GOTO;
                        }
                    }
                }
            }
            return propertyMetaDataList;
        }
    }
}
