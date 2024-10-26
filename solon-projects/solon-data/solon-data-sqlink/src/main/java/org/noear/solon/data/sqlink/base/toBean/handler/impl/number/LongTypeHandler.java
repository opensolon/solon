package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongTypeHandler implements ITypeHandler<Long>
{
    @Override
    public Long getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        long aLong = resultSet.getLong(index);
        return resultSet.wasNull() ? null : aLong;
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Long aLong) throws SQLException
    {
        preparedStatement.setLong(index, aLong);
    }
}
