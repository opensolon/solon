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
package org.noear.solon.data.sqlink.base.toBean.handler.impl.datetime;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * LocalDateTime类型处理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class LocalDateTimeTypeHandler implements ITypeHandler<LocalDateTime> {
    @Override
    public LocalDateTime getValue(ResultSet resultSet, int index, Type type) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(index);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, LocalDateTime localDateTime) throws SQLException {
        if (localDateTime == null) {
            preparedStatement.setNull(index, JDBCType.TIMESTAMP.getVendorTypeNumber());
        }
        else {
            preparedStatement.setTimestamp(index, Timestamp.valueOf(localDateTime));
        }
    }

    @Override
    public LocalDateTime castStringToTarget(String value, Type type) {
        return LocalDateTime.parse(value);
    }
}
