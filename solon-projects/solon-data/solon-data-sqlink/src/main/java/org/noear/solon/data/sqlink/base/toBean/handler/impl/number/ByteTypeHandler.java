package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteTypeHandler implements ITypeHandler<Byte>
{
    @Override
    public Byte getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        byte aByte = resultSet.getByte(index);
        return resultSet.wasNull() ? null : aByte;
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Byte aByte) throws SQLException
    {
        preparedStatement.setByte(index, aByte);
    }
}
