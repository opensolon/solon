/*
 * Copyright 2017-2025 noear.org and authors
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

/**
 * Date类型处理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class DateTypeHandler implements ITypeHandler<Date> {
    @Override
    public Date getValue(ResultSet resultSet, int index, Type type) throws SQLException {
        return resultSet.getDate(index);
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Date date) throws SQLException {
        if (date == null) {
            preparedStatement.setNull(index, JDBCType.DATE.getVendorTypeNumber());
        }
        else {
            preparedStatement.setDate(index, date);
        }
    }

    @Override
    public Date castStringToTarget(String value, Type type) {
        return Date.valueOf(value);
    }
}
