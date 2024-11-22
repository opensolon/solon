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
package org.noear.solon.data.sqlink.base.toBean.handler.impl.other;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * URL类型处理器
 *
 * @author kiryu1223
 * @since 3.0
 */
public class URLTypeHandler implements ITypeHandler<URL> {
    @Override
    public URL getValue(ResultSet resultSet, int index, Type type) throws SQLException {
        return resultSet.getURL(index);
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, URL value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(index, JDBCType.VARCHAR.getVendorTypeNumber());
        }
        else {
            preparedStatement.setURL(index, value);
        }
    }

    @Override
    public URL castStringToTarget(String value, Type type) {
        try {
            return new URL(value);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
