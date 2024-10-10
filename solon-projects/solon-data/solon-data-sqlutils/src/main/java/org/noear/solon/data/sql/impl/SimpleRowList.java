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
package org.noear.solon.data.sql.impl;

import org.noear.solon.data.sql.Row;
import org.noear.solon.data.sql.RowConverter;
import org.noear.solon.data.sql.RowList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 行列表简单实现
 *
 * @author noear
 * @since 3.0
 */
class SimpleRowList extends ArrayList<Row> implements RowList {
    @Override
    public <T> List<T> toBeanList(Class<T> type, RowConverter<T> converter) throws SQLException {
        List<T> list = new ArrayList<>();
        for (Row row : this) {
            list.add(converter.convert(row, type));
        }

        return list;
    }
}
