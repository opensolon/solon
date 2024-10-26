package org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LocalDateTimeTypeHandler implements ITypeHandler<LocalDateTime>
{
    @Override
    public LocalDateTime getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        Timestamp timestamp = resultSet.getTimestamp(index);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, LocalDateTime localDateTime) throws SQLException
    {
        preparedStatement.setTimestamp(index, Timestamp.valueOf(localDateTime));
    }
}
