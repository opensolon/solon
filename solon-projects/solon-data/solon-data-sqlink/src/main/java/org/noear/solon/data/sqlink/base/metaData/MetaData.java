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
package org.noear.solon.data.sqlink.base.metaData;

import org.noear.solon.data.sqlink.annotation.*;
import org.noear.solon.data.sqlink.base.intercept.DoNothingInterceptor;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;
import org.noear.solon.data.sqlink.base.intercept.NoInterceptor;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.core.exception.SqLinkNotFoundFieldException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

/**
 * 类型元数据
 *
 * @author kiryu1223
 * @since 3.0
 */
public class MetaData
{
    /**
     * 字段列表
     */
    private final List<FieldMetaData> propertys = new ArrayList<>();
    /**
     * 类型
     */
    private final Class<?> type;
    /**
     * 构造函器
     */
    private final Constructor<?> constructor;
    /**
     * 表名
     */
    private final String tableName;
    /**
     * 模式
     */
    private final String schema;
    /**
     * 是否为空from查询表
     */
    private final boolean isEmptyTable;

    public MetaData(Class<?> type)
    {
        try
        {
            this.type = type;

            this.constructor = !type.isAnonymousClass() ? type.getConstructor() : null;
            Table table = type.getAnnotation(Table.class);
            this.tableName = hasTableName(table) ? table.value() : type.isAnonymousClass() ? type.getSuperclass().getSimpleName() : type.getSimpleName();
            this.schema = table == null ? "" : table.schema();
            this.isEmptyTable = type.isAnnotationPresent(EmptyTable.class);
            for (PropertyDescriptor descriptor : propertyDescriptors(type))
            {
                String property = descriptor.getName();
                Field field = type.getDeclaredField(property);
                Column column = field.getAnnotation(Column.class);
                boolean notNull = column != null && column.notNull();
                String columnStr = (column == null || column.value().isEmpty()) ? property : column.value();
                UseTypeHandler useTypeHandler = field.getAnnotation(UseTypeHandler.class);
                boolean isUseTypeHandler = useTypeHandler != null;
                ITypeHandler<?> typeHandler = useTypeHandler == null ? TypeHandlerManager.get(field.getGenericType()) : TypeHandlerManager.getByHandlerType(cast(useTypeHandler.value()));
                NavigateData navigateData = null;
                Navigate navigate = field.getAnnotation(Navigate.class);
                boolean isPrimaryKey = column != null && column.primaryKey();
                InsertDefaultValue insertDefaultValue = field.getAnnotation(InsertDefaultValue.class);
                //UpdateDefaultValue updateDefaultValue = field.getAnnotation(UpdateDefaultValue.class);
                OnPut onPut = field.getAnnotation(OnPut.class);
                Interceptor<?> onPutInterceptor = (onPut == null || onPut.value() == NoInterceptor.class) ? DoNothingInterceptor.Instance : Interceptor.get(cast(onPut.value()));
                OnGet onGet = field.getAnnotation(OnGet.class);
                Interceptor<?> onGetInterceptor = (onGet == null || onGet.value() == NoInterceptor.class) ? DoNothingInterceptor.Instance : Interceptor.get(cast(onGet.value()));

                if (navigate != null)
                {
                    Class<?> navigateTargetType;
                    if (Collection.class.isAssignableFrom(field.getType()))
                    {
                        Type genericType = field.getGenericType();
                        navigateTargetType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        navigateData = new NavigateData(navigate, navigateTargetType, (Class<? extends Collection<?>>) field.getType());
                    }
                    else
                    {
                        navigateTargetType = field.getType();
                        navigateData = new NavigateData(navigate, navigateTargetType, null);
                    }
                }
                boolean ignoreColumn = field.getAnnotation(IgnoreColumn.class) != null || navigateData != null;
                propertys.add(new FieldMetaData(notNull, property, columnStr, descriptor.getReadMethod(), descriptor.getWriteMethod(), field, isUseTypeHandler, typeHandler, ignoreColumn, navigateData, isPrimaryKey, insertDefaultValue, onPutInterceptor, onGetInterceptor));
            }
        }
        catch (NoSuchFieldException | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }

    }

    private boolean hasTableName(Table table)
    {
        return table != null && !table.value().isEmpty();
    }

    private PropertyDescriptor[] propertyDescriptors(Class<?> c)
    {
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(c, Object.class);
            return beanInfo.getPropertyDescriptors();
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有字段
     */
    public List<FieldMetaData> getPropertys()
    {
        return propertys;
    }

    /**
     * 获取所有非忽略字段
     */
    public List<FieldMetaData> getNotIgnorePropertys()
    {
        return propertys.stream().filter(f -> !f.isIgnoreColumn()).collect(Collectors.toList());
    }

    /**
     * 根据字段名获取字段元数据
     *
     * @param key 字段名
     */
    public FieldMetaData getFieldMetaDataByFieldName(String key)
    {
        return propertys.stream().filter(f -> f.getProperty().equals(key)).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(key));
    }

    /**
     * 根据列名获取字段元数据
     *
     * @param columnName 列名
     */
    public FieldMetaData getFieldMetaDataByColumnName(String columnName)
    {
        return propertys.stream().filter(f -> f.getColumn().equals(columnName)).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(columnName));
    }

    /**
     * 根据getter获取字段元数据
     *
     * @param getter getter方法
     */
    public FieldMetaData getFieldMetaDataByGetter(Method getter)
    {
        return propertys.stream().filter(f -> f.getGetter().equals(getter)).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(getter));
    }

    /**
     * 根据setter获取字段元数据
     *
     * @param setter setter方法
     */
    public FieldMetaData getFieldMetaDataBySetter(Method setter)
    {
        return propertys.stream().filter(f -> f.getSetter().equals(setter)).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(setter));
    }

    /**
     * 根据getter获取列名
     *
     * @param getter getter方法
     */
    public String getColumnNameByGetter(Method getter)
    {
        return propertys.stream().filter(f -> f.getGetter().equals(getter)).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(getter)).getColumn();
    }

    /**
     * 根据setter获取列名
     *
     * @param setter setter方法
     */
    public String getColumnNameBySetter(Method setter)
    {
        return propertys.stream().filter(f -> f.getSetter().equals(setter)).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(setter)).getColumn();
    }

    /**
     * 获取主键的字段元数据
     */
    public FieldMetaData getPrimary()
    {
        return propertys.stream().filter(f -> f.isPrimaryKey()).findFirst().orElseThrow(() -> new SqLinkNotFoundFieldException(type + "找不到主键"));
    }

    /**
     * 获取实体类型
     */
    public Class<?> getType()
    {
        return type;
    }

    /**
     * 获取表名
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * 获取模式
     */
    public String getSchema()
    {
        return schema;
    }

    /**
     * 是否为空from表
     */
    public boolean isEmptyTable()
    {
        return isEmptyTable;
    }

    /**
     * 获取构造函器
     */
    public Constructor<?> getConstructor()
    {
        return constructor;
    }
}
