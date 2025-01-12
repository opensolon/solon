/*
 * Copyright 2017-2025 noear.org and authors
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
import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowIterator;
import org.noear.solon.data.sql.bound.StatementBinder;
import org.noear.solon.data.sql.*;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Sql 执行器简单实现
 *
 * @author noear
 * @since 3.0
 */
public class SimpleSqlExecutor implements SqlExecutor {
    private final DataSource dataSource;
    private final String sql;
    private final Object[] argsDef;
    private final static DefaultBinder binderDef = new DefaultBinder();

    public SimpleSqlExecutor(DataSource dataSource, String sql, Object[] args) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.argsDef = args;
    }

    @Override
    public <T> T queryValue() throws SQLException {
        return (T) queryRow(rs -> rs.getObject(1));
    }

    @Override
    public <T> List<T> queryValueList() throws SQLException {
        return (List<T>) queryRowList(rs -> rs.getObject(1));
    }

    @Override
    public <T> T queryRow(Class<T> tClass) throws SQLException {
        return queryRow((RowConverter<T>) DefaultConverter.getInstance().create(tClass));
    }

    @Override
    public <T> T queryRow(RowConverter<T> converter) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, argsDef);
            holder.rsts = holder.stmt.executeQuery();

            if (holder.rsts.next()) {
                return converter.convert(holder.rsts);
            } else {
                return null;
            }
        }
    }

    @Override
    public <T> List<T> queryRowList(Class<T> tClass) throws SQLException {
        return queryRowList(DefaultConverter.getInstance().create(tClass));
    }

    @Override
    public <T> List<T> queryRowList(RowConverter<T> converter) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, argsDef);
            holder.rsts = holder.stmt.executeQuery();

            List<T> list = new ArrayList<>();

            while (holder.rsts.next()) {
                list.add(converter.convert(holder.rsts));
            }

            return list.size() > 0 ? list : null;
        }
    }

    @Override
    public <T> RowIterator<T> queryRowIterator(int fetchSize, Class<T> tClass) throws SQLException {
        return queryRowIterator(fetchSize, DefaultConverter.getInstance().create(tClass));
    }

    @Override
    public <T> RowIterator<T> queryRowIterator(int fetchSize, RowConverter<T> converter) throws SQLException {
        CommandHolder holder = buildCommand(sql, false, true);
        binderDef.setValues(holder.stmt, argsDef);
        holder.stmt.setFetchSize(fetchSize);
        holder.rsts = holder.stmt.executeQuery();

        return new SimpleRowIterator(holder, converter);
    }

    @Override
    public int update() throws SQLException {
        return update(argsDef, binderDef);
    }

    @Override
    public <S> int update(S args, StatementBinder<S> binder) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binder.setValues(holder.stmt, args);
            return holder.stmt.executeUpdate();
        }
    }

    @Override
    public int[] updateBatch(Collection<Object[]> argsList) throws SQLException {
        return updateBatch(argsList, binderDef);
    }

    @Override
    public <T> int[] updateBatch(Collection<T> argsList, StatementBinder<T> binder) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            for (T row : argsList) {
                binder.setValues(holder.stmt, row);
                holder.stmt.addBatch();
            }
            return holder.stmt.executeBatch();
        }
    }

    @Override
    public <T> T updateReturnKey() throws SQLException {
        return updateReturnKey(argsDef, binderDef);
    }

    @Override
    public <T, S> T updateReturnKey(S args, StatementBinder<S> binder) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, true, false)) {
            binder.setValues(holder.stmt, args);
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
    protected CommandHolder buildCommand(String sql, boolean returnKeys, boolean isStream) throws SQLException {
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
     * @deprecated 3.0
     */
    @Deprecated
    @Override
    public Row queryRow() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, argsDef);
            holder.rsts = holder.stmt.executeQuery();

            MetaHolder metaHolder = new MetaHolder(holder.rsts.getMetaData());

            if (holder.rsts.next()) {
                Object[] values = new Object[metaHolder.size];

                for (int i = 1; i <= values.length; i++) {
                    values[i - 1] = holder.rsts.getObject(i);
                }

                return new SimpleRow(metaHolder, values);
            } else {
                return null;
            }
        }
    }

    /**
     * @deprecated 3.0
     */
    @Deprecated
    @Override
    public RowList queryRowList() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, argsDef);
            holder.rsts = holder.stmt.executeQuery();

            MetaHolder metaHolder = new MetaHolder(holder.rsts.getMetaData());
            RowList rowList = new SimpleRowList();

            while (holder.rsts.next()) {
                Object[] values = new Object[metaHolder.size];

                for (int i = 1; i <= values.length; i++) {
                    values[i - 1] = holder.rsts.getObject(i);
                }

                SimpleRow row = new SimpleRow(metaHolder, values);
                rowList.add(row);
            }

            return rowList.size() > 0 ? rowList : null;
        }
    }
}