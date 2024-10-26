package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoolTypeHandler implements ITypeHandler<Boolean>
{
    @Override
    public Boolean getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        boolean aBoolean = resultSet.getBoolean(index);
        return resultSet.wasNull() ? null : aBoolean;
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Boolean aBoolean) throws SQLException
    {
        preparedStatement.setBoolean(index, aBoolean);
    }
}
