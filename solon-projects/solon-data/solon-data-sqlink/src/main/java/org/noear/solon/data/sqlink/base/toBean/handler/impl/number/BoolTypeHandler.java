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
package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.reflect.Type;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * boolean类型处理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class BoolTypeHandler implements ITypeHandler<Boolean> {
    @Override
    public Boolean getValue(ResultSet resultSet, int index, Type type) throws SQLException {
        boolean aBoolean = resultSet.getBoolean(index);
        return resultSet.wasNull() ? null : aBoolean;
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Boolean aBoolean) throws SQLException {
        if (aBoolean == null) {
            preparedStatement.setNull(index, JDBCType.BOOLEAN.getVendorTypeNumber());
        }
        else {
            preparedStatement.setBoolean(index, aBoolean);
        }
    }

    @Override
    public Boolean castStringToTarget(String value, Type type) {
        return Boolean.valueOf(value);
    }
}
