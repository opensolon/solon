package org.noear.solon.data.sqlink.base.toBean.Include;


import org.noear.solon.data.sqlink.base.IConfig;
import org.noear.solon.data.sqlink.base.expression.*;
import org.noear.solon.data.sqlink.base.metaData.*;
import org.noear.solon.data.sqlink.base.session.SqlSession;
import org.noear.solon.data.sqlink.base.toBean.build.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sun.jmx.mbeanserver.Util.cast;


public class IncludeBuilder<T>
{
    protected static final Logger log = LoggerFactory.getLogger(IncludeBuilder.class);
    protected final IConfig config;
    protected final Class<T> targetClass;
    protected final Collection<T> sources;
    protected final List<IncludeSet> includes;
    protected final ISqlQueryableExpression queryable;
    protected final SqlExpressionFactory factory;
    protected final SqlSession session;

    public IncludeBuilder(IConfig config, SqlSession session, Class<T> targetClass, Collection<T> sources, List<IncludeSet> includes, ISqlQueryableExpression queryable)
    {
        this.config = config;
        this.targetClass = targetClass;
        this.sources = sources;
        this.includes = includes;
        this.queryable = queryable;
        this.factory = config.getSqlExpressionFactory();
        this.session = session;
    }

    public void include() throws InvocationTargetException, IllegalAccessException
    {
        MetaData targetClassMetaData = MetaDataCache.getMetaData(targetClass);
        Map<PropertyMetaData, Map<Object, List<T>>> cache = new HashMap<>();

        for (IncludeSet include : includes)
        {
            NavigateData navigateData = include.getColumnExpression().getPropertyMetaData().getNavigateData();
            Class<?> navigateTargetType = navigateData.getNavigateTargetType();
            PropertyMetaData selfPropertyMetaData = targetClassMetaData.getPropertyMetaDataByFieldName(navigateData.getSelfPropertyName());
            PropertyMetaData targetPropertyMetaData = MetaDataCache.getMetaData(navigateTargetType).getPropertyMetaDataByFieldName(navigateData.getTargetPropertyName());
            PropertyMetaData includePropertyMetaData = include.getColumnExpression().getPropertyMetaData();

            Map<Object, List<T>> sourcesMapList = cache.get(selfPropertyMetaData);
            if (sourcesMapList == null)
            {
                sourcesMapList = getMapList(selfPropertyMetaData);
                cache.put(selfPropertyMetaData, sourcesMapList);
            }

            switch (navigateData.getRelationType())
            {
                case OneToOne:
                    oneToOne(sourcesMapList, include, navigateData, selfPropertyMetaData, targetPropertyMetaData, includePropertyMetaData);
                    break;
                case OneToMany:
                    oneToMany(sourcesMapList, include, navigateData, selfPropertyMetaData, targetPropertyMetaData, includePropertyMetaData);
                    break;
                case ManyToOne:
                    manyToOne(sourcesMapList, include, navigateData, selfPropertyMetaData, targetPropertyMetaData, includePropertyMetaData);
                    break;
                case ManyToMany:
                    manyToMany(sourcesMapList, include, navigateData, selfPropertyMetaData, targetPropertyMetaData, includePropertyMetaData);
                    break;
            }
        }
    }

    protected void oneToOne(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, PropertyMetaData selfPropertyMetaData, PropertyMetaData targetPropertyMetaData, PropertyMetaData includePropertyMetaData) throws InvocationTargetException, IllegalAccessException
    {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfPropertyMetaData, 0)), selfPropertyMetaData.getType(), true,false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(targetPropertyMetaData, 0), warpQueryable));

        // 如果有额外条件就加入
        if (include.hasCond())
        {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression)
            {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, targetPropertyMetaData, navigateTargetType, queryableExpression);
            }
            // 简易条件
            else
            {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }

        List<Object> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<PropertyMetaData> mappingData = tempQueryable.getMappingData(config);
        // 获取从表的map
        Map<Object, Object> objectMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMap(targetPropertyMetaData.getColumn()),
                sql,
                values
        );
        // 一对一赋值
        for (Map.Entry<Object, Object> objectEntry : objectMap.entrySet())
        {
            Object key = objectEntry.getKey();
            Object value = objectEntry.getValue();
            for (T t : sourcesMapList.get(key))
            {
                includePropertyMetaData.getSetter().invoke(t, value);
            }
        }
        round(include, navigateTargetType, objectMap.values(), tempQueryable);
    }

    protected void oneToMany(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, PropertyMetaData selfPropertyMetaData, PropertyMetaData targetPropertyMetaData, PropertyMetaData includePropertyMetaData) throws InvocationTargetException, IllegalAccessException
    {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfPropertyMetaData, 0)), selfPropertyMetaData.getType(), true,false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(targetPropertyMetaData, 0), warpQueryable));
        // 如果有额外条件就加入
        if (include.hasCond())
        {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression)
            {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, targetPropertyMetaData, navigateTargetType, queryableExpression);
            }
            // 简易条件
            else
            {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }

        List<Object> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<PropertyMetaData> mappingData = tempQueryable.getMappingData(config);
        // 查询从表数据，按key进行list归类的map构建
        Map<Object, List<Object>> targetMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMapList(targetPropertyMetaData.getColumn()),
                sql,
                values
        );
        // 一对多赋值
        boolean isSet = Set.class.isAssignableFrom(navigateData.getCollectionWrapperType());
        for (Map.Entry<Object, List<Object>> objectListEntry : targetMap.entrySet())
        {
            Object key = objectListEntry.getKey();
            List<Object> value = objectListEntry.getValue();
            for (T t : sourcesMapList.get(key))
            {
                if (isSet)
                {
                    includePropertyMetaData.getSetter().invoke(t, new HashSet<>(value));
                }
                else
                {
                    includePropertyMetaData.getSetter().invoke(t, value);
                }
            }
        }
        round(include, navigateTargetType, targetMap.values(), tempQueryable);
    }

    protected void manyToOne(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, PropertyMetaData selfPropertyMetaData, PropertyMetaData targetPropertyMetaData, PropertyMetaData includePropertyMetaData) throws InvocationTargetException, IllegalAccessException
    {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfPropertyMetaData, 0)), selfPropertyMetaData.getType(), true,false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(targetPropertyMetaData, 0), warpQueryable));
        // 如果有额外条件就加入
        if (include.hasCond())
        {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression)
            {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, targetPropertyMetaData, navigateTargetType, queryableExpression);
            }
            // 简易条件
            else
            {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }

        List<Object> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<PropertyMetaData> mappingData = tempQueryable.getMappingData(config);
        // 获取目标表的map
        Map<Object, Object> objectMap = session.executeQuery(
                r -> ObjectBuilder.start(r, cast(navigateTargetType), mappingData, false, config).createMap(targetPropertyMetaData.getColumn()),
                sql,
                values
        );
        // 多对一赋值
        for (Map.Entry<Object, Object> objectEntry : objectMap.entrySet())
        {
            Object key = objectEntry.getKey();
            Object value = objectEntry.getValue();
            List<T> ts = sourcesMapList.get(key);
            for (T t : ts)
            {
                includePropertyMetaData.getSetter().invoke(t, value);
            }
        }
        round(include, navigateTargetType, objectMap.values(), tempQueryable);
    }

    protected void manyToMany(Map<Object, List<T>> sourcesMapList, IncludeSet include, NavigateData navigateData, PropertyMetaData selfPropertyMetaData, PropertyMetaData targetPropertyMetaData, PropertyMetaData includePropertyMetaData) throws InvocationTargetException, IllegalAccessException
    {
        Class<?> navigateTargetType = navigateData.getNavigateTargetType();
        Class<? extends IMappingTable> mappingTableType = navigateData.getMappingTableType();
        MetaData mappingTableMetadata = MetaDataCache.getMetaData(mappingTableType);
        String selfMappingPropertyName = navigateData.getSelfMappingPropertyName();
        PropertyMetaData selfMappingPropertyMetaData = mappingTableMetadata.getPropertyMetaDataByFieldName(selfMappingPropertyName);
        String targetMappingPropertyName = navigateData.getTargetMappingPropertyName();
        PropertyMetaData targetMappingPropertyMetaData = mappingTableMetadata.getPropertyMetaDataByFieldName(targetMappingPropertyName);
        // 查询目标表
        ISqlQueryableExpression tempQueryable = factory.queryable(navigateTargetType);
        // join中间表
        tempQueryable.addJoin(factory.join(JoinType.LEFT, factory.table(mappingTableType), factory.binary(SqlOperator.EQ, factory.column(targetPropertyMetaData, 0), factory.column(targetMappingPropertyMetaData, 1)), 1));
        // 包一层，并选择字段
        ISqlQueryableExpression warpQueryable = factory.queryable(queryable);
        warpQueryable.setSelect(factory.select(Collections.singletonList(factory.column(selfPropertyMetaData, 0)), selfPropertyMetaData.getType(), true,false));
        tempQueryable.addWhere(factory.binary(SqlOperator.IN, factory.column(selfMappingPropertyMetaData, 1), warpQueryable));

        // 增加上额外用于排序的字段
        tempQueryable.getSelect().getColumns().add(factory.column(selfMappingPropertyMetaData, 1));

        // 如果有额外条件就加入
        if (include.hasCond())
        {
            ISqlExpression cond = include.getCond();
            // 复杂条件
            if (cond instanceof ISqlQueryableExpression)
            {
                ISqlQueryableExpression queryableExpression = (ISqlQueryableExpression) cond;
                // 替换
                tempQueryable = warpItQueryable(tempQueryable, selfMappingPropertyMetaData, navigateTargetType, queryableExpression, factory.column(selfMappingPropertyMetaData, 0));
            }
            // 简易条件
            else
            {
                tempQueryable.addWhere(factory.parens(cond));
            }
        }


        List<Object> values = new ArrayList<>();
        String sql = tempQueryable.getSqlAndValue(config, values);

        tryPrint(sql);

        List<PropertyMetaData> mappingData = tempQueryable.getMappingData(config);
        //mappingData.add(selfMappingPropertyMetaData);
        Map<Object, List<Object>> targetMap = session.executeQuery(
                r -> ObjectBuilder.start(r,cast(navigateTargetType), mappingData, false, config).createMapListByAnotherKey(selfMappingPropertyMetaData),
                sql,
                values
        );

        boolean isSet = Set.class.isAssignableFrom(navigateData.getCollectionWrapperType());
        for (Map.Entry<Object, List<Object>> objectEntry : targetMap.entrySet())
        {
            Object key = objectEntry.getKey();
            List<Object> value = objectEntry.getValue();
            List<T> ts = sourcesMapList.get(key);
            for (T t : ts)
            {
                if (isSet)
                {
                    includePropertyMetaData.getSetter().invoke(t, new HashSet<>(value));
                }
                else
                {
                    includePropertyMetaData.getSetter().invoke(t, value);
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
//                includePropertyMetaData.getSetter().invoke(t, targetValues);
//            }
//        }
        round(include, navigateTargetType, targetMap.values(), tempQueryable);
    }

    private <K> Map<K, T> getMap(PropertyMetaData propertyMetaData) throws InvocationTargetException, IllegalAccessException
    {
        Map<K, T> sourcesMap = new HashMap<>();
        for (T source : sources)
        {
            K selfKey = (K) propertyMetaData.getGetter().invoke(source);
            sourcesMap.put(selfKey, source);
        }
        return sourcesMap;
    }

    private <K> Map<K, List<T>> getMapList(PropertyMetaData propertyMetaData) throws InvocationTargetException, IllegalAccessException
    {
        Map<K, List<T>> sourcesMapList = new HashMap<>();
        for (T source : sources)
        {
            K selfKey = (K) propertyMetaData.getGetter().invoke(source);
            if (!sourcesMapList.containsKey(selfKey))
            {
                List<T> list = new ArrayList<>();
                list.add(source);
                sourcesMapList.put(selfKey, list);
            }
            else
            {
                sourcesMapList.get(selfKey).add(source);
            }
        }
        return sourcesMapList;
    }

    protected ISqlQueryableExpression warpItQueryable(ISqlQueryableExpression querySqlBuilder, PropertyMetaData targetPropertyMetaData, Class<?> navigateTargetType, ISqlQueryableExpression virtualTableContext)
    {
        return warpItQueryable(querySqlBuilder, targetPropertyMetaData, navigateTargetType, virtualTableContext, null);
    }

    protected ISqlQueryableExpression warpItQueryable(ISqlQueryableExpression querySqlBuilder, PropertyMetaData targetPropertyMetaData, Class<?> navigateTargetType, ISqlQueryableExpression virtualTableContext, ISqlColumnExpression another)
    {
        ISqlOrderByExpression orderBy = virtualTableContext.getOrderBy();
        ISqlWhereExpression where = virtualTableContext.getWhere();
        ISqlLimitExpression limit = virtualTableContext.getLimit();
        if (!where.isEmpty())
        {
            for (ISqlExpression condition : where.getConditions().getConditions())
            {
                querySqlBuilder.addWhere(condition);
            }
        }
        // 包装一下窗口查询
        ISqlQueryableExpression window = factory.queryable(querySqlBuilder);
        List<ISqlExpression> selects = new ArrayList<>(2);
        selects.add(factory.constString("t0.*"));

        List<ISqlExpression> rowNumberParams = new ArrayList<>();
        rowNumberParams.add(factory.column(targetPropertyMetaData, 0));
        rowNumberParams.addAll(orderBy.getSqlOrders());
        List<String> rowNumberFunction = new ArrayList<>();
        rowNumber(rowNumberFunction, rowNumberParams);

        String rank = "-rank-";
        selects.add(factory.as(factory.template(rowNumberFunction, rowNumberParams), rank));
        window.setSelect(factory.select(selects, navigateTargetType));
        // 最外层
        ISqlQueryableExpression window2 = factory.queryable(window);
        if (another != null)
        {
            window2.getSelect().getColumns().add(another);
        }
        if (limit.onlyHasRows())
        {
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
        else if (limit.hasRowsAndOffset())
        {
            ISqlConstStringExpression _rank_ = factory.constString(config.getDisambiguation().disambiguation(rank));
            ISqlBinaryExpression right = factory.binary(SqlOperator.LE, _rank_, factory.value(limit.getOffset() + limit.getRows()));
            ISqlBinaryExpression left = factory.binary(SqlOperator.GT, _rank_, factory.value(limit.getOffset()));
            window2.addWhere(factory.binary(SqlOperator.AND, left, right));
        }
        return window2;
    }

    private void round(IncludeSet include, Class<?> navigateTargetType, Collection<?> sources, ISqlQueryableExpression main, Object... os) throws InvocationTargetException, IllegalAccessException
    {
        if (!include.getIncludeSets().isEmpty())
        {
            IncludeFactory includeFactory = config.getIncludeFactory();
            includeFactory.getBuilder(config, session, cast(navigateTargetType), sources, include.getIncludeSets(), main).include();
        }
    }

    private void round(IncludeSet include, Class<?> navigateTargetType, Collection<List<Object>> sources, ISqlQueryableExpression main) throws InvocationTargetException, IllegalAccessException
    {
        if (!include.getIncludeSets().isEmpty())
        {
            List<Object> collect = sources.stream().flatMap(o -> o.stream()).collect(Collectors.toList());
            IncludeFactory includeFactory = config.getIncludeFactory();
            includeFactory.getBuilder(config, session, cast(navigateTargetType), collect, include.getIncludeSets(), main).include();
        }
    }

    private void tryPrint(String sql)
    {
        if (config.isPrintSql())
        {
            log.info("includeQuery: ==> {}", sql);
        }
    }

    protected void rowNumber(List<String> rowNumberFunction, List<ISqlExpression> rowNumberParams)
    {
        rowNumberFunction.add("ROW_NUMBER() OVER (PARTITION BY ");
        if (rowNumberParams.size() > 1)
        {
            rowNumberFunction.add(" ORDER BY ");
        }
        for (int i = 0; i < rowNumberParams.size(); i++)
        {
            if (i < rowNumberParams.size() - 2) rowNumberFunction.add(",");
        }
        rowNumberFunction.add(")");
    }
}
