/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
