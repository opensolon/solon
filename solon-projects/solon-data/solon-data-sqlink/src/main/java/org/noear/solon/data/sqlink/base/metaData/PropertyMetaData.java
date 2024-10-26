package org.noear.solon.data.sqlink.base.metaData;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class PropertyMetaData
{
    private final String property;
    private final String column;
    private final Method getter;
    private final Method setter;
    private final Field field;
    private final boolean isGenericType;
    private final IConverter<?, ?> converter;
    private final boolean ignoreColumn;
    private final NavigateData navigateData;
    private final boolean isPrimaryKey;

    public PropertyMetaData(String property, String column, Method getter, Method setter, Field field, IConverter<?, ?> converter, boolean ignoreColumn, NavigateData navigateData, boolean isPrimaryKey)
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
        this.converter = converter;
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

    public IConverter<?, ?> getConverter()
    {
        return converter;
    }

    public boolean hasConverter()
    {
        return converter != null;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyMetaData that = (PropertyMetaData) o;
        return ignoreColumn == that.ignoreColumn && isPrimaryKey == that.isPrimaryKey && Objects.equals(property, that.property) && Objects.equals(column, that.column) && Objects.equals(getter, that.getter) && Objects.equals(setter, that.setter) && Objects.equals(field, that.field) && Objects.equals(converter, that.converter) && Objects.equals(navigateData, that.navigateData);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(property, column, getter, setter, field, converter, ignoreColumn, navigateData, isPrimaryKey);
    }
}
