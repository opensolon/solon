package org.noear.solon.data.sqlink.base.session;


import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Collection;
import java.util.List;

public interface SqlSession
{
    interface Function<T, R>
    {
        R invoke(T t) throws SQLException, NoSuchFieldException, IllegalAccessException, InvocationTargetException;
    }

    <R> R executeQuery(Function<ResultSet, R> func, String sql, Collection<Object> values);

    long executeUpdate(String sql, List<SqlValue> values);

    long executeUpdate(String sql, List<Object> values, Object... o);

    long batchExecuteUpdate(String sql, long limit, List<SqlValue> values);
}
