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

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class PropertyMetaData
{
    private final String property;
    private final String column;
    private final Method getter;
    private final Method setter;
    private final Field field;
    private final boolean isGenericType;
    private final boolean useTypeHandler;
    private final ITypeHandler<?> typeHandler;
    private final boolean ignoreColumn;
    private final NavigateData navigateData;
    private final boolean isPrimaryKey;

    public PropertyMetaData(String property, String column, Method getter, Method setter, Field field, boolean useTypeHandler, ITypeHandler<?> typeHandler, boolean ignoreColumn, NavigateData navigateData, boolean isPrimaryKey)
    {
        this.property = property;
        this.column = column;
        this.ignoreColumn = ignoreColumn;
        this.isPrimaryKey = isPrimaryKey;
        getter.setAccessible(true);
        this.getter = getter;
        setter.setAccessible(true);
        this.setter = setter;
        this.field = field;
        this.useTypeHandler = useTypeHandler;
        this.typeHandler = typeHandler;
        this.navigateData = navigateData;
        this.isGenericType = field.getGenericType() instanceof ParameterizedType;
    }

    public String getProperty()
    {
        return property;
    }

    public String getColumn()
    {
        return column;
    }

    public Method getGetter()
    {
        return getter;
    }

    public Method getSetter()
    {
        return setter;
    }

    public Field getField()
    {
        return field;
    }

    public boolean isIgnoreColumn()
    {
        return ignoreColumn;
    }

    public boolean hasNavigate()
    {
        return navigateData != null;
    }

    public NavigateData getNavigateData()
    {
        return navigateData;
    }

    public Class<?> getParentType()
    {
        return field.getDeclaringClass();
    }

    public Class<?> getType()
    {
        return field.getType();
    }

    public Type getGenericType()
    {
        return field.getGenericType();
    }

    public boolean isGenericType()
    {
        return isGenericType;
    }

    public boolean isPrimaryKey()
    {
        return isPrimaryKey;
    }

    public ITypeHandler<?> getTypeHandler()
    {
        return typeHandler;
    }

    public boolean isUseTypeHandler()
    {
        return useTypeHandler;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyMetaData that = (PropertyMetaData) o;
        return isGenericType == that.isGenericType && useTypeHandler == that.useTypeHandler && ignoreColumn == that.ignoreColumn && isPrimaryKey == that.isPrimaryKey && Objects.equals(property, that.property) && Objects.equals(column, that.column) && Objects.equals(getter, that.getter) && Objects.equals(setter, that.setter) && Objects.equals(field, that.field) && Objects.equals(typeHandler, that.typeHandler) && Objects.equals(navigateData, that.navigateData);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(property, column, getter, setter, field, isGenericType, useTypeHandler, typeHandler, ignoreColumn, navigateData, isPrimaryKey);
    }
}
