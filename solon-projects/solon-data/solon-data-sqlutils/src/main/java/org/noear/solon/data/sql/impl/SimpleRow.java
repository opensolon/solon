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
public class SimpleRow implements Row {
    private final ResultSetMetaData meta;
    private final Object[] data;

    public SimpleRow(ResultSetMetaData meta, Object[] data) {
        this.meta = meta;
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public ResultSetMetaData getMeta() {
        return meta;
    }

    @Override
    public Object[] getData() {
        return data;
    }

    @Override
    public String getName(int columnIdx) throws SQLException {
        return meta.getColumnLabel(columnIdx);
    }

    @Override
    public Object getValue(int columnIndex) {
        return data[columnIndex - 1];
    }

    @Override
    public Map<String, Object> toMap() throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            map.put(getName(i), getValue(i));
        }
        return map;
    }
}
