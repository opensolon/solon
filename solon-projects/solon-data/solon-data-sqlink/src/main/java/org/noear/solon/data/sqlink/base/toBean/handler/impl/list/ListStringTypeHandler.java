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
package org.noear.solon.data.sqlink.base.toBean.handler.impl.list;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kiryu1223
 * @since 3.0
 */
public class ListStringTypeHandler implements ITypeHandler<List<String>> {
    @Override
    public List<String> getValue(ResultSet resultSet, int index, Type type) throws SQLException {
        String string = resultSet.getString(index);
        return Arrays.stream(string.split(",")).collect(Collectors.toList());
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, List<String> strings) throws SQLException {
        preparedStatement.setString(index, String.join(",", strings));
    }

    @Override
    public List<String> castStringToTarget(String value, Type type) {
        return Arrays.stream(value.split(",")).collect(Collectors.toList());
    }
}
