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

import org.noear.solon.Solon;
import org.noear.solon.data.sql.Row;
import org.noear.solon.data.sql.RowIterator;
import org.noear.solon.data.sql.RowList;
import org.noear.solon.data.sql.SqlExecutor;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sql 执行器简单实现
 *
 * @author noear
 * @since 3.0
 */
public class SimpleSqlExecutor implements SqlExecutor {
    private final DataSource dataSource;
    private final String sql;
    private final Object[] args;

    public SimpleSqlExecutor(DataSource dataSource, String sql, Object[] args) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.args = args;
    }

    @Override
    public <T> T queryValue() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, args, false, false)) {
            holder.rsts = holder.stmt.executeQuery();

            if (holder.rsts.next()) {
                return (T) holder.rsts.getObject(1);
            }

            return null;
        }
    }

    @Override
    public <T> List<T> queryValueList() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, args, false, false)) {
            holder.rsts = holder.stmt.executeQuery();

            List<T> list = new ArrayList<>();

            while (holder.rsts.next()) {
                list.add((T) holder.rsts.getObject(1));
            }

            return list.size() > 0 ? list : null;
        }
    }

    @Override
    public Row queryRow() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, args, false, false)) {
            holder.rsts = holder.stmt.executeQuery();

            if (holder.rsts.next()) {
                return holder.getRow();
            } else {
                return null;
            }
        }
    }

    @Override
    public RowList queryRowList() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, args, false, false)) {
            holder.rsts = holder.stmt.executeQuery();

            RowList rowList = new SimpleRowList();

            while (holder.rsts.next()) {
                rowList.add(holder.getRow());
            }

            return rowList.size() > 0 ? rowList : null;
        }
    }

    @Override
    public RowIterator queryRowIterator(int fetchSize) throws SQLException {
        CommandHolder holder = buildCommand(sql, args, false, true);
        holder.stmt.setFetchSize(fetchSize);
        holder.rsts = holder.stmt.executeQuery();

        return new SimpleRowIterator(holder);
    }

    @Override
    public int update() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, args, false, false)) {
            return holder.stmt.executeUpdate();
        }
    }

    @Override
    public int[] updateBatch(Collection<Object[]> argsList) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, argsList, false, false)) {
            return holder.stmt.executeBatch();
        }
    }

    @Override
    public <T> T updateReturnKey() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, args, true, false)) {
            holder.stmt.executeUpdate();
            holder.rsts = holder.stmt.getGeneratedKeys();

            if (holder.rsts.next()) {
                return (T) holder.rsts.getObject(1);
            } else {
                return null;
            }
        }
    }

    /////////////////////

    /**
     * 构建预处理
     */
    protected CommandHolder buildCommand(String sql, Object args, boolean returnKeys, boolean isStream) throws SQLException {
        CommandHolder holder = new CommandHolder();
        holder.conn = getConnection();

        if (isStream) {
            //流
            if (sql.startsWith("{call")) {
                holder.stmt = holder.conn.prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } else {
                holder.stmt = holder.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
        } else {
            if (returnKeys) {
                //插入
                holder.stmt = holder.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                //其它
                if (sql.startsWith("{call")) {
                    holder.stmt = holder.conn.prepareCall(sql);
                } else {
                    holder.stmt = holder.conn.prepareStatement(sql);
                }
            }
        }

        if (args instanceof Collection) {
            //批处理
            Collection<Object[]> argsList = (Collection<Object[]>) args;
            for (Object[] row : argsList) {
                for (int i = 0; i < row.length; i++) {
                    setObject(holder.stmt, i + 1, row[i]);
                }
                holder.stmt.addBatch();
            }
        } else {
            //单处理
            Object[] row = (Object[]) args;
            for (int i = 0; i < row.length; i++) {
                setObject(holder.stmt, i + 1, row[i]);
            }
        }


        return holder;
    }


    /**
     * 获取连接（为转换提供重写机会）
     */
    protected Connection getConnection() throws SQLException {
        if (Solon.app() == null) {
            return dataSource.getConnection();
        } else {
            return TranUtils.getConnectionProxy(dataSource);
        }
    }

    /**
     * 填充数据（为转换提供重写机会）
     *
     * @param columnIdx 列顺位（从1开始）
     */
    protected void setObject(PreparedStatement stmt, int columnIdx, Object val) throws SQLException {
        if (val == null) {
            stmt.setNull(columnIdx, Types.VARCHAR);
        } else if (val instanceof java.util.Date) {
            if (val instanceof java.sql.Date) {
                stmt.setDate(columnIdx, (java.sql.Date) val);
            } else if (val instanceof java.sql.Timestamp) {
                stmt.setTimestamp(columnIdx, (java.sql.Timestamp) val);
            } else {
                java.util.Date v1 = (java.util.Date) val;
                stmt.setTimestamp(columnIdx, new java.sql.Timestamp(v1.getTime()));
            }
        } else {
            stmt.setObject(columnIdx, val);
        }
    }
}
