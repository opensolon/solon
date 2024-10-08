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

    @Override
    public Map<String, Object> toMap() throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= _metaHolder.size; i++) {
            map.put(getName(i), getValue(i));
        }

        return map;
    }
}
