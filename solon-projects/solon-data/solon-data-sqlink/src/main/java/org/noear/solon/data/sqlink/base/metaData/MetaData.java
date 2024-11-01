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

import org.noear.solon.data.sqlink.base.annotation.*;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;

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
 * @author kiryu1223
 * @since 3.0
 */
public class MetaData
{
    private final List<PropertyMetaData> propertys = new ArrayList<>();
    private final Class<?> type;
    private final Constructor<?> constructor;
    private final String tableName;
    private final String schema;
    private final boolean isEmptyTable;

    public MetaData(Class<?> type)
    {
        try
        {
            this.type = type;

            this.constructor = !type.isAnonymousClass() ? type.getConstructor() : null;
            Table table = type.getAnnotation(Table.class);
            this.tableName = (table == null || table.value().isEmpty()) ? type.getSimpleName() : table.value();
            this.schema = table == null ? "" : table.schema();
            this.isEmptyTable = type.isAnnotationPresent(EmptyTable.class);
            for (PropertyDescriptor descriptor : propertyDescriptors(type))
            {
                String property = descriptor.getName();
                Field field = type.getDeclaredField(property);
                Column column = field.getAnnotation(Column.class);
                String columnStr = (column == null || column.value().isEmpty()) ? property : column.value();
                UseTypeHandler useTypeHandler = field.getAnnotation(UseTypeHandler.class);
                boolean isUseTypeHandler = useTypeHandler != null;
                ITypeHandler<?> typeHandler = useTypeHandler == null ? TypeHandlerManager.get(field.getGenericType()) : TypeHandlerManager.getByHandlerType(cast(useTypeHandler.value()));
                NavigateData navigateData = null;
                Navigate navigate = field.getAnnotation(Navigate.class);
                boolean isPrimaryKey = column != null && column.primaryKey();
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
                propertys.add(new PropertyMetaData(property, columnStr, descriptor.getReadMethod(), descriptor.getWriteMethod(), field, isUseTypeHandler, typeHandler, ignoreColumn, navigateData, isPrimaryKey));
            }
        }
        catch (NoSuchFieldException | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }

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

    public List<PropertyMetaData> getPropertys()
    {
        return propertys;
    }

    public List<PropertyMetaData> getNotIgnorePropertys()
    {
        return propertys.stream().filter(f -> !f.isIgnoreColumn()).collect(Collectors.toList());
    }

    public PropertyMetaData getPropertyMetaDataByFieldName(String key)
    {
        return propertys.stream().filter(f -> f.getProperty().equals(key)).findFirst().orElseThrow(() -> new RuntimeException(key));
    }

    public PropertyMetaData getPropertyMetaDataByColumnName(String asName)
    {
        return propertys.stream().filter(f -> f.getColumn().equals(asName)).findFirst().orElseThrow(() -> new RuntimeException(asName));
    }

    public PropertyMetaData getPropertyMetaDataByGetter(Method getter)
    {
        return getPropertyMetaDataByColumnName(getColumnNameByGetter(getter));
    }

    public PropertyMetaData getPropertyMetaDataBySetter(Method setter)
    {
        return getPropertyMetaDataByColumnName(getColumnNameBySetter(setter));
    }

    public String getColumnNameByGetter(Method getter)
    {
        return propertys.stream().filter(f -> f.getGetter().equals(getter)).findFirst().orElseThrow(() -> new RuntimeException(getter.toGenericString())).getColumn();
    }

    public String getColumnNameBySetter(Method setter)
    {
        return propertys.stream().filter(f -> f.getSetter().equals(setter)).findFirst().orElseThrow(() -> new RuntimeException(setter.toGenericString())).getColumn();
    }

    public PropertyMetaData getPrimary()
    {
        return propertys.stream().filter(f -> f.isPrimaryKey()).findFirst().orElse(null);
    }

    public Class<?> getType()
    {
        return type;
    }

    public String getTableName()
    {
        return tableName;
    }

    public String getSchema()
    {
        return schema;
    }

    public boolean isEmptyTable()
    {
        return isEmptyTable;
    }

    public Constructor<?> getConstructor()
    {
        return constructor;
    }
}
