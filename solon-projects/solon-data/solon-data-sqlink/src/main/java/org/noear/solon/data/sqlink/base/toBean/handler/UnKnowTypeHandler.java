package org.noear.solon.data.sqlink.base.toBean.handler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnKnowTypeHandler<T> implements ITypeHandler<T>
{
    @Override
    public T getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        return (T) resultSet.getObject(index, c);
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, T value) throws SQLException
    {
        preparedStatement.setObject(index, value);
    }
}
