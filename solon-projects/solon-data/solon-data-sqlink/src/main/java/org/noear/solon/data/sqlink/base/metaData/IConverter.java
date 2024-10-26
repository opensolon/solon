package org.noear.solon.data.sqlink.base.metaData;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface IConverter<J, D>
{
    D toDb(J value, PropertyMetaData propertyMetaData);

    J toJava(D value, PropertyMetaData propertyMetaData);

    default Class<D> getDbType()
    {
        Type[] interfaces = this.getClass().getGenericInterfaces();
        Type type = interfaces[0];
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Type dbType = pType.getActualTypeArguments()[1];
            return (Class<D>) dbType;
        }
        else
        {
            throw new RuntimeException(String.format("无法找到%s的第%s个泛型类型", type, 1));
        }
    }

    default Class<J> getJavaType()
    {
        Type[] interfaces = this.getClass().getGenericInterfaces();
        Type type = interfaces[0];
        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Type dbType = pType.getActualTypeArguments()[0];
            return (Class<J>) dbType;
        }
        else
        {
            throw new RuntimeException(String.format("无法找到%s的第%s个泛型类型", type, 10));
        }
    }
}
