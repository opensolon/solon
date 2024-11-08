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
 * 字段元数据
 *
 * @author kiryu1223
 * @since 3.0
 */
public class FieldMetaData {
    /**
     * 字段名
     */
    private final String property;
    /**
     * 列名
     */
    private final String column;
    /**
     * getter
     */
    private final Method getter;
    /**
     * setter
     */
    private final Method setter;
    /**
     * 字段
     */
    private final Field field;
    /**
     * 是否是泛型类型
     */
    private final boolean isGenericType;
    /**
     * 是否显示指定类型处理器
     */
    private final boolean useTypeHandler;
    /**
     * 类型处理器
     */
    private final ITypeHandler<?> typeHandler;
    /**
     * 是否为忽略列
     */
    private final boolean ignoreColumn;
    /**
     * 导航数据
     */
    private final NavigateData navigateData;
    /**
     * 是否为主键
     */
    private final boolean isPrimaryKey;

    private final Type genericType;

    public FieldMetaData(String property, String column, Method getter, Method setter, Field field, boolean useTypeHandler, ITypeHandler<?> typeHandler, boolean ignoreColumn, NavigateData navigateData, boolean isPrimaryKey) {
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
        this.genericType = field.getGenericType();
        this.isGenericType = genericType instanceof ParameterizedType;
    }

    /**
     * 属性名
     */
    public String getProperty() {
        return property;
    }

    /**
     * 列名
     */
    public String getColumn() {
        return column;
    }

    /**
     * getter
     */
    public Method getGetter() {
        return getter;
    }

    /**
     * setter
     */
    public Method getSetter() {
        return setter;
    }

    /**
     * 字段
     */
    public Field getField() {
        return field;
    }

    /**
     * 是否为忽略列
     */
    public boolean isIgnoreColumn() {
        return ignoreColumn;
    }

    /**
     * 是否有导航属性
     */
    public boolean hasNavigate() {
        return navigateData != null;
    }

    /**
     * 导航数据
     */
    public NavigateData getNavigateData() {
        return navigateData;
    }

    /**
     * 父类类型
     */
    public Class<?> getParentType() {
        return field.getDeclaringClass();
    }

    /**
     * 字段类型
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * 字段泛型类型
     */
    public Type getGenericType() {
        return genericType;
    }

    /**
     * 是否为泛型类型
     */
    public boolean isGenericType() {
        return isGenericType;
    }

    /**
     * 是否为主键
     */
    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * 类型处理器
     */
    public ITypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    /**
     * 是否显示指定类型处理器
     */
    public boolean isUseTypeHandler() {
        return useTypeHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldMetaData that = (FieldMetaData) o;
        return isGenericType == that.isGenericType && useTypeHandler == that.useTypeHandler && ignoreColumn == that.ignoreColumn && isPrimaryKey == that.isPrimaryKey && Objects.equals(property, that.property) && Objects.equals(column, that.column) && Objects.equals(getter, that.getter) && Objects.equals(setter, that.setter) && Objects.equals(field, that.field) && Objects.equals(typeHandler, that.typeHandler) && Objects.equals(navigateData, that.navigateData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property, column, getter, setter, field, isGenericType, useTypeHandler, typeHandler, ignoreColumn, navigateData, isPrimaryKey);
    }
}
