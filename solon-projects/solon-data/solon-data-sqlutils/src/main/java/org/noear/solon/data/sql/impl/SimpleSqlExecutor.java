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
import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowConverterFactory;
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
    private final Object[] args;
    private final static DefaultBinder binderDef = new DefaultBinder();

    public SimpleSqlExecutor(DataSource dataSource, String sql, Object[] args) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.args = args;
    }

    @Override
    public <T> T queryValue() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
            holder.rsts = holder.stmt.executeQuery();

            if (holder.rsts.next()) {
                return (T) holder.rsts.getObject(1);
            }

            return null;
        }
    }

    @Override
    public <T> List<T> queryValueList() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
            holder.rsts = holder.stmt.executeQuery();

            List<T> list = new ArrayList<>();

            while (holder.rsts.next()) {
                list.add((T) holder.rsts.getObject(1));
            }

            return list.size() > 0 ? list : null;
        }
    }

    @Deprecated
    @Override
    public Row queryRow() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
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

    @Override
    public RowList queryRowList() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
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

    @Override
    public <T> T queryRow(Class<T> tClass) throws SQLException {
        return queryRowDo((r, t) -> (RowConverter<T>) DefaultConverter.getInstance().create(r, tClass));
    }

    @Override
    public <T> T queryRow(RowConverter<T> converter) throws SQLException {
        return queryRowDo((r, t) -> converter);
    }

    private  <T> T queryRowDo(RowConverterFactory<T> factory) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
            holder.rsts = holder.stmt.executeQuery();

            RowConverter<T> converter = factory.create(holder.rsts, null);

            if (holder.rsts.next()) {
                return converter.convert(holder.rsts);
            } else {
                return null;
            }
        }
    }

    @Override
    public <T> List<T> queryRowList(Class<T> tClass) throws SQLException {
        return queryRowListDo((r, t) -> DefaultConverter.getInstance().create(r, tClass));
    }

    @Override
    public <T> List<T> queryRowList(RowConverter<T> converter) throws SQLException {
        return queryRowListDo((r, t) -> converter);
    }

    private  <T> List<T> queryRowListDo(RowConverterFactory<T> factory) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
            holder.rsts = holder.stmt.executeQuery();

            RowConverter<T> converter = factory.create(holder.rsts, null);
            List<T> list = new ArrayList<>();

            while (holder.rsts.next()) {
                list.add(converter.convert(holder.rsts));
            }

            return list.size() > 0 ? list : null;
        }
    }

    @Override
    public RowIterator<Map<String,Object>> queryRowIterator(int fetchSize) throws SQLException {
        return queryRowIteratorDo(fetchSize, (r, t) -> DefaultConverter.getInstance().create(r, Map.class));
    }

    @Override
    public <T> RowIterator<T> queryRowIterator(int fetchSize, Class<T> tClass) throws SQLException {
        return queryRowIteratorDo(fetchSize, (r, t) -> DefaultConverter.getInstance().create(r, tClass));
    }

    @Override
    public <T> RowIterator<T> queryRowIterator(int fetchSize, RowConverter<T> converter) throws SQLException {
        return queryRowIteratorDo(fetchSize, (r,t) -> converter);
    }

    private  <T> RowIterator<T> queryRowIteratorDo(int fetchSize, RowConverterFactory<T> factory) throws SQLException {
        CommandHolder holder = buildCommand(sql, false, true);
        binderDef.setValues(holder.stmt, args);
        holder.stmt.setFetchSize(fetchSize);
        holder.rsts = holder.stmt.executeQuery();

        RowConverter<T> converter = factory.create(holder.rsts, null);

        return new SimpleRowIterator(holder, converter);
    }

    @Override
    public int update() throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            binderDef.setValues(holder.stmt, args);
            return holder.stmt.executeUpdate();
        }
    }

    @Override
    public int[] updateBatch(Collection<Object[]> argsList) throws SQLException {
        try (CommandHolder holder = buildCommand(sql, false, false)) {
            for (Object[] args : argsList) {
                binderDef.setValues(holder.stmt, args);
                holder.stmt.addBatch();
            }
            return holder.stmt.executeBatch();
        }
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
        try (CommandHolder holder = buildCommand(sql, true, false)) {
            binderDef.setValues(holder.stmt, args);
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
}