package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntTypeHandler implements ITypeHandler<Integer>
{
    @Override
    public Integer getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        int anInt = resultSet.getInt(index);
        return resultSet.wasNull() ? null : anInt;
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Integer integer) throws SQLException
    {
        preparedStatement.setInt(index, integer);
    }
}
