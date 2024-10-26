package org.noear.solon.data.sqlink.base.toBean.handler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ITypeHandler<T>
{
    T getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException;

    void setValue(PreparedStatement preparedStatement, int index, T value) throws SQLException;

    default Type getActualType()
    {
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces)
        {
            if (genericInterface instanceof ParameterizedType)
            {
                ParameterizedType anInterface = (ParameterizedType) genericInterface;
                if (anInterface.getRawType() == ITypeHandler.class)
                {
                    return anInterface.getActualTypeArguments()[0];
                }
            }
        }
        throw new RuntimeException("未知的ITypeHandler<T>实现类:" + this.getClass());
    }
}
