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

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询语句表达式
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface ISqlQueryableExpression extends ISqlTableExpression {
    @Override
    default ISqlQueryableExpression copy(SqLinkConfig config) {
        SqlExpressionFactory factory = config.getSqlExpressionFactory();
        ISqlQueryableExpression queryableExpression = factory.queryable(getSelect().copy(config), getFrom().copy(config), getJoins().copy(config), getWhere().copy(config), getGroupBy().copy(config), getHaving().copy(config), getOrderBy().copy(config), getLimit().copy(config));
        queryableExpression.setChanged(getChanged());
        return queryableExpression;
    }

    /**
     * 设置是否已经发生变化
     */
    void setChanged(boolean changed);

    /**
     * 获取是否已经发生变化
     */
    boolean getChanged();

    /**
     * 添加where条件
     */
    void addWhere(ISqlExpression cond);

    /**
     * 添加join条件
     */
    void addJoin(ISqlJoinExpression join);

    /**
     * 设置group by
     */
    void setGroup(ISqlGroupByExpression group);

    /**
     * 添加having条件
     */
    void addHaving(ISqlExpression cond);

    /**
     * 添加orderBy列
     */
    void addOrder(ISqlOrderExpression order);

    /**
     * 设置select
     */
    void setSelect(ISqlSelectExpression newSelect);

    /**
     * 设置limit
     */
    void setLimit(long offset, long rows);

    /**
     * 设置是否去重
     */
    void setDistinct(boolean distinct);

    /**
     * 获取from
     */
    ISqlFromExpression getFrom();

    /**
     * 获取查询列数量（from + joins）
     */
    int getOrderedCount();

    /**
     * 获取where
     */
    ISqlWhereExpression getWhere();

    /**
     * 获取groupBy
     */
    ISqlGroupByExpression getGroupBy();

    /**
     * 获取join
     */
    ISqlJoinsExpression getJoins();

    /**
     * 获取select
     */
    ISqlSelectExpression getSelect();

    /**
     * 获取orderBy
     */
    ISqlOrderByExpression getOrderBy();

    /**
     * 获取limit
     */
    ISqlLimitExpression getLimit();

    /**
     * 获取having
     */
    ISqlHavingExpression getHaving();

    /**
     * 获取查询列的类（from + joins）
     */
    List<Class<?>> getOrderedClass();

    /**
     * 获取映射的列
     */
    default List<FieldMetaData> getMappingData() {
        if (getChanged()) {
            return getMappingData0();
        }
        else {
            ISqlTableExpression sqlTableExpression = getFrom().getSqlTableExpression();
            if (sqlTableExpression instanceof ISqlRealTableExpression || sqlTableExpression instanceof ISqlWithExpression) {
                return getMappingData0();
            }
            else {
                ISqlQueryableExpression tableExpression = (ISqlQueryableExpression) sqlTableExpression;
                return tableExpression.getMappingData();
            }
        }
    }

    default List<FieldMetaData> getMappingData0() {
        List<Class<?>> orderedClass = getOrderedClass();
        Class<?> target = getMainTableClass();
        MetaData metaData = MetaDataCache.getMetaData(target);
        if (orderedClass.contains(target)) {
            return metaData.getNotIgnorePropertys();
        }
        else {
            List<FieldMetaData> fieldMetaDataList = new ArrayList<>();
            for (FieldMetaData sel : metaData.getNotIgnorePropertys()) {
                GOTO:
                for (MetaData data : MetaDataCache.getMetaData(orderedClass)) {
                    for (FieldMetaData noi : data.getNotIgnorePropertys()) {
                        if (noi.getColumn().equals(sel.getColumn()) && noi.getType().equals(sel.getType())) {
                            fieldMetaDataList.add(sel);
                            break GOTO;
                        }
                    }
                }
            }
            return fieldMetaDataList;
        }
    }
}
