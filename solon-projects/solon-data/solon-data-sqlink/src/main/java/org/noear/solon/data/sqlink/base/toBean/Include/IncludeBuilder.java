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
package org.noear.solon.data.sqlink.base.toBean.Include;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.*;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.base.session.SqlValue;
import org.noear.solon.data.sqlink.base.toBean.build.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sun.jmx.mbeanserver.Util.cast;

/**
 * 对象抓取构建器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class IncludeBuilder<T> {
    protected static final Logger log = LoggerFactory.getLogger(IncludeBuilder.class);
    protected final SqLinkConfig config;
    protected final Class<T> targetClass;
    protected final Collection<T> sources;
    protected final List<IncludeSet> includes;
    protected final ISqlQueryableExpression queryable;
    protected final SqlExpressionFactory factory;
    protected final SqlSession session;

    public IncludeBuilder(SqLinkConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable) {
        this.config = config;
        this.targetClass = targetClass;
        this.sources = sources;
        this.includes = includes;
        this.queryable = queryable;
        this.factory = config.getSqlExpressionFactory();
        this.session = session;
    }

    /**
     * 执行抓取
     */
    public void include() throws InvocationTargetException, IllegalAccessException {
        MetaData targetClassMetaData = MetaDataCache.getMetaData(targetClass);
        Map<FieldMetaData, Map<Object, List<T>>> cache = new HashMap<>();

        for (IncludeSet include : includes) {
            NavigateData navigateData = include.getColumnExpression().getFieldMetaData().getNavigateData();
            Class<?> navigateTargetType = navigateData.getNavigateTargetType();
            FieldMetaData selfFieldMetaData = targetClassMetaData.getFieldMetaDataByFieldName(navigateData.getSelfFieldName());
            FieldMetaData targetFieldMetaData = MetaDataCache.getMetaData(navigateTargetType).getFieldMetaDataByFieldName(navigateData.getTargetFieldName());
            FieldMetaData includeFieldMetaData = include.getColumnExpression().getFieldMetaData();

            Map<Object, List<T>> sourcesMapList = cache.get(selfFieldMetaData);
            if (sourcesMapList == null) {
                sourcesMapList = getMapList(selfFieldMetaData);
                cache.put(selfFieldMetaData, sourcesMapList);
            }

            switch (navigateData.getRelationType()) {
                // 一对一
                case OneToOne:
                    oneToOne(sourcesMapList, include, navigateData, selfFieldMetaData, targetFieldMetaData, includeFieldMetaData);
                    break;
                // 一对多
                case OneToMany:
                    oneToMany(sourcesMapList, include, navigateData, selfFieldMetaData, targetFieldMetaData, includeFieldMetaData);
                    break;
                // 多对一
                case ManyToOne:
                    manyToOne(sourcesMapList, include, navigateData, selfFieldMetaData, targetFieldMetaData, includeFieldMetaData);
                    break;
                // 多对多
                case ManyToMany:
                    manyToMany(sourcesMapList, include, navigateData, selfFieldMetaData, targetFieldMetaData, includeFieldMetaData);
                    break;
            }
        }
    }

    protected void oneToOne(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, FieldMetaData selfFieldMetaData, FieldMetaData targetFieldMetaData, FieldMetaData includeFieldMetaData) throws InvocationTargetException, IllegalAccessException {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfFieldMetaData, warpQueryable.getFrom().getAsName())), selfFieldMetaData.getType(), true, false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(targetFieldMetaData, tempQueryable.getFrom().getAsName()), warpQueryable));

        // 如果有额外条件就加入
        if (include.hasCond()) {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression) {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, targetFieldMetaData, navigateTargetType, queryableExpression);
            }
            // 简易条件
            else {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }

        List<SqlValue> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<FieldMetaData> mappingData = tempQueryable.getMappingData();
        // 获取从表的map
        Map<Object, Object> objectMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMap(targetFieldMetaData.getColumn()),
                sql,
                values
        );
        // 一对一赋值
        for (Map.Entry<Object, Object> objectEntry : objectMap.entrySet()) {
            Object key = objectEntry.getKey();
            Object value = objectEntry.getValue();
            for (T t : sourcesMapList.get(key)) {
                includeFieldMetaData.getSetter().invoke(t, value);
            }
        }
        round(include, navigateTargetType, objectMap.values(), tempQueryable);
    }

    protected void oneToMany(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, FieldMetaData selfFieldMetaData, FieldMetaData targetFieldMetaData, FieldMetaData includeFieldMetaData) throws InvocationTargetException, IllegalAccessException {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfFieldMetaData, warpQueryable.getFrom().getAsName())), selfFieldMetaData.getType(), true, false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(targetFieldMetaData, tempQueryable.getFrom().getAsName()), warpQueryable));
        // 如果有额外条件就加入
        if (include.hasCond()) {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression) {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, targetFieldMetaData, navigateTargetType, queryableExpression);
            }
            // 简易条件
            else {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }

        List<SqlValue> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<FieldMetaData> mappingData = tempQueryable.getMappingData();
        // 查询从表数据，按key进行list归类的map构建
        Map<Object, List<Object>> targetMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMapList(targetFieldMetaData.getColumn()),
                sql,
                values
        );
        // 一对多赋值
        boolean isSet = Set.class.isAssignableFrom(navigateData.getCollectionWrapperType());
        for (Map.Entry<Object, List<Object>> objectListEntry : targetMap.entrySet()) {
            Object key = objectListEntry.getKey();
            List<Object> value = objectListEntry.getValue();
            for (T t : sourcesMapList.get(key)) {
                if (isSet) {
                    includeFieldMetaData.getSetter().invoke(t, new HashSet<>(value));
                }
                else {
                    includeFieldMetaData.getSetter().invoke(t, value);
                }
            }
        }
        round(include, navigateTargetType, targetMap.values(), tempQueryable);
    }

    protected void manyToOne(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, FieldMetaData selfFieldMetaData, FieldMetaData targetFieldMetaData, FieldMetaData includeFieldMetaData) throws InvocationTargetException, IllegalAccessException {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfFieldMetaData, warpQueryable.getFrom().getAsName())), selfFieldMetaData.getType(), true, false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(targetFieldMetaData, tempQueryable.getFrom().getAsName()), warpQueryable));
        // 如果有额外条件就加入
        if (include.hasCond()) {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression) {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, targetFieldMetaData, navigateTargetType, queryableExpression);
            }
            // 简易条件
            else {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }

        List<SqlValue> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<FieldMetaData> mappingData = tempQueryable.getMappingData();
        // 获取目标表的map
        Map<Object, Object> objectMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMap(targetFieldMetaData.getColumn()),
                sql,
                values
        );
        // 多对一赋值
        for (Map.Entry<Object, Object> objectEntry : objectMap.entrySet()) {
            Object key = objectEntry.getKey();
            Object value = objectEntry.getValue();
            List<T> ts = sourcesMapList.get(key);
            for (T t : ts) {
                includeFieldMetaData.getSetter().invoke(t, value);
            }
        }
        round(include, navigateTargetType, objectMap.values(), tempQueryable);
    }

    protected void manyToMany(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, FieldMetaData selfFieldMetaData, FieldMetaData targetFieldMetaData, FieldMetaData includeFieldMetaData) throws InvocationTargetException, IllegalAccessException {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        Class<? extends IMappingTable> mappingTableType = navigateData.getMappingTableType();
        MetaData mappingTableMetadata = MetaDataCache.getMetaData(mappingTableType);
        String mappingTableAsName = mappingTableMetadata.getTableName().substring(0, 1).toLowerCase();
        String selfMappingPropertyName = navigateData.getSelfMappingFieldName();
        FieldMetaData selfMappingFieldMetaData = mappingTableMetadata.getFieldMetaDataByFieldName(selfMappingPropertyName);
        String targetMappingPropertyName = navigateData.getTargetMappingFieldName();
        FieldMetaData targetMappingFieldMetaData = mappingTableMetadata.getFieldMetaDataByFieldName(targetMappingPropertyName);
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // join中间表
        tempQueryable.addJoin(factory.join(JoinType.LEFT, factory.table(mappingTableType), factory.binary(SqlOperator.EQ, factory.column(targetFieldMetaData, tempQueryable.getFrom().getAsName()), factory.column(targetMappingFieldMetaData, mappingTableAsName)), mappingTableAsName));
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfFieldMetaData, warpQueryable.getFrom().getAsName())), selfFieldMetaData.getType(), true, false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(selfMappingFieldMetaData, mappingTableAsName), warpQueryable));

        // 增加上额外用于排序的字段
        tempQueryable.getSelect().getColumns().add(factory.column(selfMappingFieldMetaData, mappingTableAsName));

        // 如果有额外条件就加入
        if (include.hasCond()) {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression) {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, selfMappingFieldMetaData, navigateTargetType, queryableExpression, factory.column(selfMappingFieldMetaData, mappingTableAsName));
            }
            // 简易条件
            else {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }


        List<SqlValue> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<FieldMetaData> mappingData = tempQueryable.getMappingData();
        //mappingData.add(selfMappingFieldMetaData);
        Map<Object, List<Object>> targetMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMapListByAnotherKey(selfMappingFieldMetaData),
                sql,
                values
        );

        boolean isSet = Set.class.isAssignableFrom(navigateData.getCollectionWrapperType());
        for (Map.Entry<Object, List<Object>> objectEntry : targetMap.entrySet()) {
            Object key = objectEntry.getKey();
            List<Object> value = objectEntry.getValue();
            List<T> ts = sourcesMapList.get(key);
            for (T t : ts) {
                if (isSet) {
                    includeFieldMetaData.getSetter().invoke(t, new HashSet<>(value));
                }
                else {
                    includeFieldMetaData.getSetter().invoke(t, value);
                }
            }
        }
//        for (Map.Entry<Object, List<T>> objectEntry : sourcesMapList.entrySet())
//        {
//            Object key = objectEntry.getKey();
//            List<T> value = objectEntry.getValue();
//            List<Object> targetValues = targetMap.get(key);
//            for (T t : value)
//            {
//                includeFieldMetaData.getSetter().invoke(t, targetValues);
//            }
//        }
        round(include, navigateTargetType, targetMap.values(), tempQueryable);
    }

    protected <K> Map<K, T> getMap(FieldMetaData fieldMetaData) throws InvocationTargetException, IllegalAccessException {
        Map<K, T> sourcesMap = new HashMap<>();
        for (T source : sources) {
            K selfKey = (K) fieldMetaData.getGetter().invoke(source);
            sourcesMap.put(selfKey, source);
        }
        return sourcesMap;
    }

    protected <Key> Map<Key, List<T>> getMapList(FieldMetaData fieldMetaData) throws InvocationTargetException, IllegalAccessException {
        Map<Key, List<T>> sourcesMapList = new HashMap<>();
        for (T source : sources) {
            Key selfKey = (Key) fieldMetaData.getGetter().invoke(source);
            if (!sourcesMapList.containsKey(selfKey)) {
                List<T> list = new ArrayList<>();
                list.add(source);
                sourcesMapList.put(selfKey, list);
            }
            else {
                sourcesMapList.get(selfKey).add(source);
            }
        }
        return sourcesMapList;
    }

    protected ISqlQueryableExpression warpItQueryable(ISqlQueryableExpression querySqlBuilder, FieldMetaData targetFieldMetaData, Class<?> navigateTargetType, ISqlQueryableExpression virtualTableContext) {
        return warpItQueryable(querySqlBuilder, targetFieldMetaData, navigateTargetType, virtualTableContext, null);
    }

    protected ISqlQueryableExpression warpItQueryable(ISqlQueryableExpression queryableExpression, FieldMetaData targetFieldMetaData, Class<?> navigateTargetType, ISqlQueryableExpression virtualTableContext, ISqlColumnExpression another) {
        ISqlOrderByExpression orderBy = virtualTableContext.getOrderBy();
        ISqlWhereExpression where = virtualTableContext.getWhere();
        ISqlLimitExpression limit = virtualTableContext.getLimit();
        if (!where.isEmpty()) {
            for (ISqlExpression condition : where.getConditions().getConditions()) {
                queryableExpression.addWhere(condition);
            }
        }
        // 包装一下窗口查询
        ISqlQueryableExpression window = factory.queryable(queryableExpression);
        List<ISqlExpression> selects = new ArrayList<>(2);
        selects.add(factory.constString("t0.*"));

        List<ISqlExpression> rowNumberParams = new ArrayList<>();
        rowNumberParams.add(factory.column(targetFieldMetaData, queryableExpression.getFrom().getAsName()));
        rowNumberParams.addAll(orderBy.getSqlOrders());
        List<String> rowNumberFunction = new ArrayList<>();
        rowNumber(rowNumberFunction, rowNumberParams);

        String rank = "-rank-";
        selects.add(factory.as(factory.template(rowNumberFunction, rowNumberParams), rank));
        window.setSelect(factory.select(selects, navigateTargetType));
        // 最外层
        ISqlQueryableExpression window2 = factory.queryable(window);
        if (another != null) {
            window2.getSelect().getColumns().add(another);
        }
        if (limit.onlyHasRows()) {
//            long offset = limit.getOffset();
//            long rows = limit.getRows();
//
//            SqlBinaryExpression skip = null;
//            SqlBinaryExpression take;
//            if (offset > 0)
//            {
//                skip = factory.binary(SqlOperator.GT, _rank_, factory.value(offset));
//                take = factory.binary(SqlOperator.LE, _rank_, factory.value(offset + rows));
//            }
//            else
//            {
//                take = factory.binary(SqlOperator.LE, _rank_, factory.value(rows));
//            }
//            SqlExpression limitCond;
//            if (skip != null)
//            {
//                limitCond = factory.binary(SqlOperator.AND, skip, take);
//            }
//            else
//            {
//                limitCond = take;
//            }
            ISqlConstStringExpression _rank_ = factory.constString(config.getDisambiguation().disambiguation(rank));
            window2.addWhere(factory.binary(SqlOperator.LE, _rank_, factory.value(limit.getRows())));
        }
        else if (limit.hasRowsAndOffset()) {
            ISqlConstStringExpression _rank_ = factory.constString(config.getDisambiguation().disambiguation(rank));
            ISqlBinaryExpression right = factory.binary(SqlOperator.LE, _rank_, factory.value(limit.getOffset() + limit.getRows()));
            ISqlBinaryExpression left = factory.binary(SqlOperator.GT, _rank_, factory.value(limit.getOffset()));
            window2.addWhere(factory.binary(SqlOperator.AND, left, right));
        }
        return window2;
    }

    protected void round(IncludeSet include, Class<?> navigateTargetType, Collection<?> sources, ISqlQueryableExpression main, Object... os) throws InvocationTargetException, IllegalAccessException {
        if (!include.getIncludeSets().isEmpty()) {
            IncludeFactory includeFactory = config.getIncludeFactory();
            includeFactory.getBuilder(config, session, cast(navigateTargetType), sources, include.getIncludeSets(), main).include();
        }
    }

    protected void round(IncludeSet include, Class<?> navigateTargetType, Collection<List<Object>> sources, ISqlQueryableExpression main) throws InvocationTargetException, IllegalAccessException {
        if (!include.getIncludeSets().isEmpty()) {
            List<Object> collect = sources.stream().flatMap(o -> o.stream()).collect(Collectors.toList());
            IncludeFactory includeFactory = config.getIncludeFactory();
            includeFactory.getBuilder(config, session, cast(navigateTargetType), collect, include.getIncludeSets(), main).include();
        }
    }

    private void tryPrint(String sql) {
        if (config.isPrintSql()) {
            log.info("includeQuery: ==> {}", sql);
        }
    }

    protected void rowNumber(List<String> rowNumberFunction, List<ISqlExpression> rowNumberParams) {
        rowNumberFunction.add("ROW_NUMBER() OVER (PARTITION BY ");
        if (rowNumberParams.size() > 1) {
            rowNumberFunction.add(" ORDER BY ");
        }
        for (int i = 0; i < rowNumberParams.size(); i++) {
            if (i < rowNumberParams.size() - 2) rowNumberFunction.add(",");
        }
        rowNumberFunction.add(")");
    }
}
