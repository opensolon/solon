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

import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaData;
import org.noear.solon.data.sqlink.base.metaData.MetaDataCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 表达式工厂,所有表达式都应该从工厂创建
 *
 * @author kiryu1223
 * @since 3.0
 */
public interface SqlExpressionFactory {
    /**
     * 创建别名表达式
     *
     * @param expression 表达式
     * @param asName     别名
     */
    ISqlAsExpression as(ISqlExpression expression, String asName);

    /**
     * 创建列表达式
     *
     * @param fieldMetaData 字段元数据
     */
    default ISqlColumnExpression column(FieldMetaData fieldMetaData) {
        return column(fieldMetaData, null);
    }

    /**
     * 创建列表达式
     *
     * @param fieldMetaData 字段元数据
     * @param tableAsName   表别名
     */
    ISqlColumnExpression column(FieldMetaData fieldMetaData, AsName tableAsName);

    /**
     * 创建条件表达式
     */
    ISqlConditionsExpression condition();

    /**
     * 创建from表达式
     *
     * @param sqlTable 表表达式
     * @param asName   表别名
     */
    ISqlFromExpression from(ISqlTableExpression sqlTable, AsName asName);

    /**
     * 创建分组group by表达式
     */
    ISqlGroupByExpression groupBy();

    /**
     * 创建分组表达式
     *
     * @param columns 分组选择的字段
     */
    default ISqlGroupByExpression groupBy(LinkedHashMap<String, ISqlExpression> columns) {
        ISqlGroupByExpression groupByExpression = groupBy();
        groupByExpression.setColumns(columns);
        return groupByExpression;
    }

    /**
     * 创建having表达式
     */
    ISqlHavingExpression having();

    /**
     * 创建join表达式
     *
     * @param joinType   join类型
     * @param joinTable  join表
     * @param conditions join条件
     * @param asName     join别名
     */
    ISqlJoinExpression join(JoinType joinType, ISqlTableExpression joinTable, ISqlExpression conditions, AsName asName);

    /**
     * 创建join集合表达式
     */
    ISqlJoinsExpression Joins();

    /**
     * 创建limit表达式
     */
    ISqlLimitExpression limit();

    /**
     * 创建limit表达式
     *
     * @param offset 偏移量
     * @param rows   行数
     */
    default ISqlLimitExpression limit(long offset, long rows) {
        ISqlLimitExpression limit = limit();
        limit.setOffset(offset);
        limit.setRows(rows);
        return limit;
    }

    /**
     * 创建order by表达式
     */
    ISqlOrderByExpression orderBy();

    /**
     * 创建order表达式
     *
     * @param expression 表达式
     */
    default ISqlOrderExpression order(ISqlExpression expression) {
        return order(expression, true);
    }

    /**
     * 创建order表达式
     *
     * @param expression 表达式
     * @param asc        是否为升序
     */
    ISqlOrderExpression order(ISqlExpression expression, boolean asc);

    /**
     * 创建查询表达式
     *
     * @param target 目标表
     */
    default ISqlQueryableExpression queryable(Class<?> target, AsName asName) {
        return queryable(from(table(target), asName));
    }

    /**
     * 创建查询表达式
     *
     * @param from from表达式
     */
    default ISqlQueryableExpression queryable(ISqlFromExpression from) {
        return queryable(select(from.getSqlTableExpression().getMainTableClass(),from.getAsName()), from, Joins(), where(), groupBy(), having(), orderBy(), limit());
    }

    /**
     * 创建查询表达式
     *
     * @param select select表达式
     * @param from   from表达式
     */
    default ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from) {
        return queryable(select, from, Joins(), where(), groupBy(), having(), orderBy(), limit());
    }

    /**
     * 创建查询表达式
     *
     * @param table 表表达式
     */
    default ISqlQueryableExpression queryable(ISqlTableExpression table, AsName asName) {
        return queryable(from(table, asName));
    }

    /**
     * 创建查询表达式
     *
     * @param select  select表达式
     * @param from    from表达式
     * @param joins   join表达式集合
     * @param where   where表达式
     * @param groupBy 组表达式
     * @param having  having表达式
     * @param orderBy 排序表达式
     * @param limit   limit表达式
     */
    ISqlQueryableExpression queryable(ISqlSelectExpression select, ISqlFromExpression from, ISqlJoinsExpression joins, ISqlWhereExpression where, ISqlGroupByExpression groupBy, ISqlHavingExpression having, ISqlOrderByExpression orderBy, ISqlLimitExpression limit);

    /**
     * 创建表表达式
     *
     * @param tableClass 实体表类型
     */
    ISqlRealTableExpression table(Class<?> tableClass);

    /**
     * 创建select表达式
     *
     * @param target 目标类
     */
    default ISqlSelectExpression select(Class<?> target,AsName asName) {
        return select(getColumnByClass(target,asName), target, false, false);
    }

    /**
     * 创建select表达式
     *
     * @param column 选择的列
     * @param target 目标类
     */
    default ISqlSelectExpression select(List<ISqlExpression> column, Class<?> target) {
        return select(column, target, false, false);
    }

    /**
     * 创建select表达式
     *
     * @param column     选择的列
     * @param target     目标类
     * @param isSingle   是否为单列查询
     * @param isDistinct 是否为去重查询
     */
    ISqlSelectExpression select(List<ISqlExpression> column, Class<?> target, boolean isSingle, boolean isDistinct);

    /**
     * 创建where表达式
     */
    default ISqlWhereExpression where() {
        return where(condition());
    }

    /**
     * 创建where表达式
     *
     * @param conditions 条件表达式
     */
    ISqlWhereExpression where(ISqlConditionsExpression conditions);

    /**
     * 创建set表达式
     *
     * @param column 需要set的列
     * @param value  需要set的值
     */
    ISqlSetExpression set(ISqlColumnExpression column, ISqlExpression value);

    /**
     * 创建set集合表达式
     */
    ISqlSetsExpression sets();

    /**
     * 创建值表达式
     *
     * @param value 值或值集合
     */
    default ISqlValueExpression AnyValue(Object value) {
        // 如果是集合就返回集合值表达式
        if (value instanceof Collection<?>) {
            return value((Collection<?>) value);
        }
        // 否则就返回单个值表达式
        else {
            return value(value);
        }
    }

    /**
     * 创建值表达式
     *
     * @param value 值
     */
    ISqlSingleValueExpression value(Object value);

    /**
     * 创建集合值表达式
     *
     * @param value 值集合
     */
    ISqlCollectedValueExpression value(Collection<?> value);

    /**
     * 创建模板表达式
     *
     * @param templates   字符串模板列表
     * @param expressions 表达式列表
     */
    ISqlTemplateExpression template(List<String> templates, List<? extends ISqlExpression> expressions);

    /**
     * 创建二元表达式
     *
     * @param operator SQL运算符
     * @param left     左表达式
     * @param right    右表达式
     */
    ISqlBinaryExpression binary(SqlOperator operator, ISqlExpression left, ISqlExpression right);

    /**
     * 创建一元表达式
     *
     * @param operator   SQL运算符
     * @param expression 表达式
     */
    ISqlUnaryExpression unary(SqlOperator operator, ISqlExpression expression);

    /**
     * 创建括号表达式
     *
     * @param expression 表达式
     */
    ISqlParensExpression parens(ISqlExpression expression);

    /**
     * 创建常量字符串表达式
     *
     * @param s 字符串
     */
    ISqlConstStringExpression constString(String s);

    /**
     * 创建类型表达式
     *
     * @param c 类型
     */
    ISqlTypeExpression type(Class<?> c);

    ISqlWithExpression with(ISqlQueryableExpression queryable, String name);

    ISqlWithsExpression withs();

    ISqlUnionExpression union(ISqlQueryableExpression queryable, boolean all);

    ISqlUnionsExpression unions();

    ISqlRecursionExpression recursion(ISqlQueryableExpression queryable, String parentId, String childId, int level);

    ISqlUpdateExpression update(ISqlFromExpression from, ISqlJoinsExpression joins, ISqlSetsExpression sets, ISqlWhereExpression where);

    default ISqlUpdateExpression update(Class<?> target, AsName asName) {
        return update(from(table(target), asName), Joins(), sets(), where());
    }

    ISqlDynamicColumnExpression dynamicColumn(String column, AsName tableAsName);

    /**
     * 将实体类转换为列表达式集合
     */
    default List<ISqlExpression> getColumnByClass(Class<?> target,AsName asName) {
        MetaData metaData = MetaDataCache.getMetaData(target);
        List<FieldMetaData> property = metaData.getNotIgnorePropertys();
        List<ISqlExpression> columns = new ArrayList<>(property.size());
        String as = metaData.getTableName().substring(0, 1).toLowerCase();
        for (FieldMetaData data : property) {
            columns.add(column(data, asName));
        }
        return columns;
    }
}
