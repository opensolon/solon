package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortTypeHandler implements ITypeHandler<Short>
{
    @Override
    public Short getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        short aShort = resultSet.getShort(index);
        return resultSet.wasNull() ? null : aShort;
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Short aShort) throws SQLException
    {
        preparedStatement.setShort(index, aShort);
    }
}
