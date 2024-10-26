package org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateTypeHandler implements ITypeHandler<Date>
{
    @Override
    public Date getValue(ResultSet resultSet, int index,Class<?> c) throws SQLException
    {
        return resultSet.getDate(index);
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Date date) throws SQLException
    {
        preparedStatement.setDate(index,date);
    }
}
