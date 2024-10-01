package org.noear.solon.data.sqlutils.impl;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * 数据遍历器
 *
 * @author noear
 * @since 3.0
 */
public class DataIterator implements Iterator<Map<String,Object>>, Closeable {
    private final CommandPrepare prepare;

    public DataIterator(CommandPrepare prepare) {
        this.prepare = prepare;
    }

    private Map<String, Object> rowTmp;

    @Override
    public boolean hasNext() {
        try {
            if (prepare.rsts.next()) {
                rowTmp = prepare.getRow();
            } else {
                rowTmp = null;
            }

            return rowTmp != null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> next() {
        return rowTmp;
    }

    @Override
    public void remove() {
        this.close();
    }

    @Override
    public void close() {
        prepare.close();
    }
}