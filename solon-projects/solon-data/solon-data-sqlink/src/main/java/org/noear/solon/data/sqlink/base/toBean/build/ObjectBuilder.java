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
package org.noear.solon.data.sqlink.base.toBean.build;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;
import org.noear.solon.data.sqlink.base.metaData.FieldMetaData;
import org.noear.solon.data.sqlink.base.toBean.beancreator.AbsBeanCreator;
import org.noear.solon.data.sqlink.base.toBean.beancreator.ISetterCaller;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

/**
 * 返回数据创建器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class ObjectBuilder<T> {
    /**
     * 结果集
     */
    private final ResultSet resultSet;
    /**
     * 目标类型
     */
    private final Class<T> target;
    /**
     * 属性元数据列表
     */
    private final List<FieldMetaData> fieldMetaDataList;
    /**
     * 是否是单列返回
     */
    private final boolean isSingle;
    /**
     * 配置
     */
    private final SqLinkConfig config;

    /**
     * 创建构建器
     */
    public static <T> ObjectBuilder<T> start(ResultSet resultSet, Class<T> target, List<FieldMetaData> fieldMetaDataList, boolean isSingle, SqLinkConfig config) {
        return new ObjectBuilder<>(resultSet, target, fieldMetaDataList, isSingle, config);
    }

    private ObjectBuilder(ResultSet resultSet, Class<T> target, List<FieldMetaData> fieldMetaDataList, boolean isSingle, SqLinkConfig config) {
        this.resultSet = resultSet;
        this.target = target;
        this.fieldMetaDataList = fieldMetaDataList;
        this.isSingle = isSingle;
        this.config = config;
        //this.valueGetter = config.getValueGetter();
    }

    /**
     * 创建Map[key,T]返回
     *
     * @param column key的列名
     */
    public <Key> Map<Key, T> createMap(String column) throws SQLException, InvocationTargetException, IllegalAccessException {
        AbsBeanCreator<T> beanCreator = config.getBeanCreatorFactory().get(target);
        Supplier<T> creator = beanCreator.getBeanCreator();
        Map<String, Integer> indexMap = getIndexMap();
        Map<Key, T> hashMap = new HashMap<>();
        while (resultSet.next()) {
            T t = creator.get();
            Key key = null;
            for (FieldMetaData metaData : fieldMetaDataList) {
                Object value = convertValue(metaData, indexMap.get(metaData.getColumn()));
                if (column.equals(metaData.getColumn())) {
                    key = (Key) value;
                }
                if (value != null) metaData.getSetter().invoke(t, value);
            }
            if (key != null) hashMap.put(key, t);
        }
        return hashMap;
    }

    /**
     * 创建Map[key,List[T]]返回
     *
     * @param keyColumn key的列名
     */
    public <Key> Map<Key, List<T>> createMapList(String keyColumn) throws SQLException, InvocationTargetException, IllegalAccessException {
        AbsBeanCreator<T> beanCreator = config.getBeanCreatorFactory().get(target);
        Supplier<T> creator = beanCreator.getBeanCreator();
        Map<String, Integer> indexMap = getIndexMap();
        // System.out.println(indexMap);
        Map<Key, List<T>> hashMap = new HashMap<>();
        while (resultSet.next()) {
            T t = creator.get();
            Key key = null;
            for (FieldMetaData metaData : fieldMetaDataList) {
                String column = metaData.getColumn();
                //System.out.println(column);
                Object value = convertValue(metaData, indexMap.get(column));
                if (keyColumn.equals(metaData.getColumn())) {
                    key = (Key) value;
                }
                if (value != null) metaData.getSetter().invoke(t, value);
            }
            if (key != null) {
                if (!hashMap.containsKey(key)) {
                    List<T> tempList = new ArrayList<>();
                    tempList.add(t);
                    hashMap.put(key, tempList);
                }
                else {
                    hashMap.get(key).add(t);
                }
            }
        }
        return hashMap;
    }

    /**
     * 创建Map[key,List[T]]返回
     *
     * @param anotherKeyColumn key的元数据
     */
    public <Key> Map<Key, List<T>> createMapListByAnotherKey(FieldMetaData anotherKeyColumn) throws SQLException, InvocationTargetException, IllegalAccessException {
        AbsBeanCreator<T> beanCreator = config.getBeanCreatorFactory().get(target);
        Supplier<T> creator = beanCreator.getBeanCreator();
        Map<String, Integer> indexMap = getIndexMap();
        int anotherKeyIndex = indexMap.get(anotherKeyColumn.getColumn());
        Map<Key, List<T>> hashMap = new HashMap<>();
        while (resultSet.next()) {
            T t = creator.get();
            //Key key = resultSet.getObject(anotherKeyColumn.getColumn(), (Class<? extends Key>) upperClass(anotherKeyColumn.getType()));
            Key key = (Key) convertValue(anotherKeyColumn, anotherKeyIndex);
            for (FieldMetaData metaData : fieldMetaDataList) {
                Object value = convertValue(metaData, indexMap.get(metaData.getColumn()));
                if (value != null) metaData.getSetter().invoke(t, value);
//                if (anotherKeyColumn.equals(metaData.getColumn()))
//                {
//                    key = (Key) value;
//                }
//                else
//                {
//                    metaData.getSetter().invoke(t, value);
//                }
            }
            if (!hashMap.containsKey(key)) {
                List<T> tempList = new ArrayList<>();
                tempList.add(t);
                hashMap.put(key, tempList);
            }
            else {
                hashMap.get(key).add(t);
            }
        }
        return hashMap;
    }

    /**
     * 创建List[T]返回
     */
    public List<T> createList() throws SQLException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        // 如果是单列
        if (isSingle) {
            return getSingleList();
        }
        // 如果是多列
        else {
            return getClassList();
        }
    }

    /**
     * 单列返回
     */
    private List<T> getSingleList() throws SQLException {
        List<T> list = new ArrayList<>();
        ITypeHandler<T> typeHandler = TypeHandlerManager.get(target);
        while (resultSet.next()) {
            T t = typeHandler.getValue(resultSet, 1, target);
            list.add(t);
        }
        return list;
    }

    /**
     * 多列返回
     */
    private List<T> getClassList() throws SQLException, IllegalAccessException, InvocationTargetException {
        AbsBeanCreator<T> beanCreator = config.getBeanCreatorFactory().get(target);
        Supplier<T> creator = beanCreator.getBeanCreator();
        Map<String, Integer> indexMap = getIndexMap();
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            T t = creator.get();
            for (FieldMetaData metaData : fieldMetaDataList) {
                Object value = convertValue(metaData, indexMap.get(metaData.getColumn()));
                if (value != null) {
                    ISetterCaller<T> beanSetter = beanCreator.getBeanSetter(metaData.getProperty());
                    beanSetter.call(t, value);
                    //metaData.getSetter().invoke(t, value);
                }
            }
            list.add(t);
        }

        return list;
    }

    /**
     * 获取列的索引
     */
    private Map<String, Integer> getIndexMap() throws SQLException {
        Map<String, Integer> indexMap = new HashMap<>();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            String columnLabel = resultSetMetaData.getColumnLabel(i);
            indexMap.put(columnLabel, i);
        }
        return indexMap;
    }

    private Object convertValue(FieldMetaData metaData, int index) throws SQLException {
        ITypeHandler<?> typeHandler = metaData.getTypeHandler();
        Object value = typeHandler.getValue(resultSet, index, metaData.getGenericType());
        Interceptor<?> onSelectGet = metaData.getOnGet();
        return onSelectGet.doIntercept(cast(value), config);
    }
}
