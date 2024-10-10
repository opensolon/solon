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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 行简单实现
 *
 * @author noear
 * @since 3.0
 */
class SimpleRow implements Row {
    private final MetaHolder _metaHolder;
    private final Object[] _data;

    public SimpleRow(MetaHolder metaHolder, Object[] data) {
        this._metaHolder = metaHolder;
        this._data = data;
    }

    @Override
    public int size() {
        return _metaHolder.size;
    }

    @Override
    public ResultSetMetaData meta() {
        return _metaHolder.meta;
    }

    @Override
    public Object[] data() {
        return _data;
    }

    @Override
    public String getName(int columnIdx) throws SQLException {
        return _metaHolder.getName(columnIdx);
    }

    @Override
    public int getNameColumnIdx(String name) throws SQLException {
        return _metaHolder.getNameColumnIdx(name);
    }

    @Override
    public Object getValue(int columnIndex) throws SQLException {
        return _data[columnIndex - 1];
    }

    @Override
    public Object getValue(String name) throws SQLException {
        int idx = getNameColumnIdx(name);
        if (idx < 1) {
            throw new SQLException("Column '" + name + "' not found");
        }

        return getValue(idx);
    }


    /**
     * 转为 Map
     */
    @Override
    public Map<String, Object> toMap() throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int cI = 1; cI <= size(); cI++) {
            map.put(getName(cI), getValue(cI));
        }

        return map;
    }

    /**
     * 转为 Bean
     *
     * @param type      类型
     * @param converter 转换器
     */
    @Override
    public  <T> T toBean(Class<T> type, Converter converter) throws SQLException {
        return (T) converter.convert(this, type);
    }

    /**
     * 转为 Bean
     *
     * @param type 类型
     */
    @Override
    public  <T> T toBean(Class<T> type) throws SQLException {
        return toBean(type, DefaultConverter.getInstance());
    }
}