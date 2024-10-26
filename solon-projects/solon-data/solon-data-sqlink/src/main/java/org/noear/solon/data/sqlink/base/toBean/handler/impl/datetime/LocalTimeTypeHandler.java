package org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class LocalTimeTypeHandler implements ITypeHandler<LocalTime>
{
    @Override
    public LocalTime getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        Time time = resultSet.getTime(index);
        return time == null ? null : time.toLocalTime();
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, LocalTime localTime) throws SQLException
    {
        preparedStatement.setTime(index, Time.valueOf(localTime));
    }
}
