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
import org.noear.solon.data.sql.*;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Sql 工具类简单实现
 *
 * @author noear
 * @since 3.0
 */
public class SimpleSqlUtils implements SqlUtils {
    private final DataSource dataSource;

    public SimpleSqlUtils(DataSource dataSource) {
        assert dataSource != null;
        this.dataSource = dataSource;
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
     * 获取数据（为转换提供重写机会）
     *
     * @param columnIdx 列顺位（从1开始）
     */
    protected Object getObject(CommandHolder holder, int columnIdx) throws SQLException {
        return holder.rsts.getObject(columnIdx);
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

    /**
     * 构建预处理
     */
    protected CommandHolder buildCommand(String sql, Object args, boolean returnKeys, boolean isStream) throws SQLException {
        CommandHolder holder = new CommandHolder(this);
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

    @Override
    public SqlExecutor sql(String sql, Object... args) {
        return new SimpleSqlExecutor(this, sql, args);
    }
}