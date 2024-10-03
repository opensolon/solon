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

import org.noear.solon.data.sql.SqlUtils;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * @author noear
 * @since 3.0
 */
public class SimpleSqlUtilsImpl implements SqlUtils {
    private final DataSource dataSource;

    public SimpleSqlUtilsImpl(DataSource dataSource) {
        assert dataSource != null;
        this.dataSource = dataSource;
    }

    /**
     * 获取连接（为转换提供重写机会）
     */
    protected Connection getConnection() throws SQLException {
        return TranUtils.getConnectionProxy(dataSource);
    }

    /**
     * 获取数据（为转换提供重写机会）
     */
    protected Object getObject(CommandPrepare prepare, int idx) throws SQLException {
        return prepare.rsts.getObject(idx);
    }

    /**
     * 填充数据（为转换提供重写机会）
     */
    protected void setObject(PreparedStatement stmt, int idx, Object val) throws SQLException {
        if (val == null) {
            stmt.setNull(idx, Types.VARCHAR);
        } else if (val instanceof java.util.Date) {
            if (val instanceof java.sql.Date) {
                stmt.setDate(idx, (java.sql.Date) val);
            } else if (val instanceof java.sql.Timestamp) {
                stmt.setTimestamp(idx, (java.sql.Timestamp) val);
            } else {
                java.util.Date v1 = (java.util.Date) val;
                stmt.setTimestamp(idx, new java.sql.Timestamp(v1.getTime()));
            }
        } else {
            stmt.setObject(idx, val);
        }
    }

    /**
     * 构建预处理
     */
    protected CommandPrepare buildPrepare(String sql, Object args, boolean returnKeys, boolean isStream) throws SQLException {
        CommandPrepare prepare = new CommandPrepare(this);
        prepare.conn = getConnection();

        if (isStream) {
            //流
            if (sql.startsWith("{call")) {
                prepare.stmt = prepare.conn.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } else {
                prepare.stmt = prepare.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
        } else {
            if (returnKeys) {
                //插入
                prepare.stmt = prepare.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                //其它
                if (sql.startsWith("{call")) {
                    prepare.stmt = prepare.conn.prepareCall(sql);
                } else {
                    prepare.stmt = prepare.conn.prepareStatement(sql);
                }
            }
        }

        if (args instanceof Collection) {
            //批处理
            List<Object[]> rowList = (List<Object[]>) args;
            for (Object[] row : rowList) {
                for (int i = 0; i < row.length; i++) {
                    setObject(prepare.stmt, i + 1, row[i]);
                }
                prepare.stmt.addBatch();
            }
        } else {
            //单处理
            Object[] row = (Object[]) args;
            for (int i = 0; i < row.length; i++) {
                setObject(prepare.stmt, i + 1, row[i]);
            }
        }


        return prepare;
    }


    @Override
    public Object selectValue(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, false, false)) {
            prepare.rsts = prepare.stmt.executeQuery();

            if (prepare.rsts.next()) {
                return getObject(prepare, 1);
            }

            return null;
        }
    }

    @Override
    public List<Object> selectValueArray(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, false, false)) {
            prepare.rsts = prepare.stmt.executeQuery();

            List<Object> list = new ArrayList<>();

            while (prepare.rsts.next()) {
                list.add(getObject(prepare, 1));
            }

            return list.size() > 0 ? list : null;
        }
    }

    @Override
    public Map<String, Object> selectRow(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, false, false)) {
            prepare.rsts = prepare.stmt.executeQuery();

            if (prepare.rsts.next()) {
                return prepare.getRow();
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Map<String, Object>> selectRowList(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, false, false)) {
            prepare.rsts = prepare.stmt.executeQuery();

            List<Map<String, Object>> rowList = new ArrayList<>();

            while (prepare.rsts.next()) {
                rowList.add(prepare.getRow());
            }

            return rowList.size() > 0 ? rowList : null;
        }
    }

    @Override
    public Iterator<Map<String, Object>> selectRowStream(String sql, int fetchSize, Object... args) throws SQLException {
        CommandPrepare prepare = buildPrepare(sql, args, false, true);
        prepare.stmt.setFetchSize(fetchSize);
        prepare.rsts = prepare.stmt.executeQuery();

        return new DataIterator(prepare);
    }

    @Override
    public int insert(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, false, false)) {
            return prepare.stmt.executeUpdate();
        }
    }

    @Override
    public long insertReturnKey(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, true, false)) {
            prepare.stmt.executeUpdate();
            prepare.rsts = prepare.stmt.getGeneratedKeys();

            if (prepare.rsts.next()) {
                return prepare.rsts.getLong(1);
            } else {
                return -1L;
            }
        }
    }

    @Override
    public int execute(String sql, Object... args) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, args, false, false)) {
            return prepare.stmt.executeUpdate();
        }
    }

    @Override
    public int[] executeBatch(String sql, Collection<Object[]> argsList) throws SQLException {
        try (CommandPrepare prepare = buildPrepare(sql, argsList, false, false)) {
            return prepare.stmt.executeBatch();
        }
    }
}