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
import org.noear.solon.data.sql.SqlCommand;
import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowIterator;
import org.noear.solon.data.sql.bound.StatementBinder;
import org.noear.solon.data.sql.*;
import org.noear.solon.data.sql.intercept.SqlInterceptor;
import org.noear.solon.data.tran.TranUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * Sql 执行器简单实现
 *
 * @author noear
 * @since 3.0
 */
public class SimpleSqlExecutor implements SqlExecutor {
    private final static DefaultBinder DEFAULT_BINDER = new DefaultBinder();

    private final DataSource dataSource;
    private final String commandText;
    private SqlCommand command;
    private SqlInterceptor interceptor;

    public SimpleSqlExecutor(DataSource dataSource, String sql) {
        this.dataSource = dataSource;
        this.commandText = sql;
    }

    /**
     * 拦截
     */
    public SimpleSqlExecutor intercept(SqlInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }


    @Override
    public SqlExecutor params(Object... args) {
        this.command = new SqlCommand(commandText, args, DEFAULT_BINDER);
        return this;
    }

    @Override
    public <S> SqlExecutor params(S args, StatementBinder<S> binder) {
        this.command = new SqlCommand(commandText, args, binder);
        return this;
    }

    @Override
    public SqlExecutor params(Collection<Object[]> argsList) {
        this.command = new SqlCommand(commandText, argsList, DEFAULT_BINDER);
        return this;
    }

    @Override
    public <S> SqlExecutor params(Collection<S> argsList, Supplier<StatementBinder<S>> binderSupplier) {
        this.command = new SqlCommand(commandText, argsList, binderSupplier.get());
        return this;
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
        return queryRow((RowConverter<T>) SqlConfiguration.getConverter().create(tClass));
    }

    @Override
    public <T> T queryRow(RowConverter<T> converter) throws SQLException {
        if (interceptor == null) {
            return (T) queryRowDo(command, converter);
        } else {
            return (T) interceptor.doIntercept(command, (cmd) -> {
                return queryRowDo(cmd, converter);
            });
        }
    }

    protected <T> Object queryRowDo(SqlCommand cmd, RowConverter<T> converter) throws SQLException {
        try (StatementHolder holder = beginStatement(cmd, false, false)) {
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
        return queryRowList(SqlConfiguration.getConverter().create(tClass));
    }

    @Override
    public <T> List<T> queryRowList(RowConverter<T> converter) throws SQLException {
        if (interceptor == null) {
            return queryRowListDo(command, converter);
        } else {
            return (List<T>) interceptor.doIntercept(command, (cmd) -> {
                return queryRowListDo(cmd, converter);
            });
        }
    }

    protected <T> List<T> queryRowListDo(SqlCommand cmd, RowConverter<T> converter) throws SQLException {
        try (StatementHolder holder = beginStatement(cmd, false, false)) {

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
        return queryRowIterator(fetchSize, SqlConfiguration.getConverter().create(tClass));
    }

    @Override
    public <T> RowIterator<T> queryRowIterator(int fetchSize, RowConverter<T> converter) throws SQLException {
        if (interceptor == null) {
            return queryRowIteratorDo(command, fetchSize, converter);
        } else {
            return (RowIterator<T>) interceptor.doIntercept(command, (cmd) -> {
                return queryRowIteratorDo(cmd, fetchSize, converter);
            });
        }
    }

    protected <T> RowIterator<T> queryRowIteratorDo(SqlCommand cmd, int fetchSize, RowConverter<T> converter) throws SQLException {
        StatementHolder holder = beginStatement(cmd, false, true);
        holder.stmt.setFetchSize(fetchSize);
        holder.rsts = holder.stmt.executeQuery();

        return new SimpleRowIterator(holder, converter);
    }

    @Override
    public int update() throws SQLException {
        if (interceptor == null) {
            return updateDo(command);
        } else {
            return (int) interceptor.doIntercept(command, (cmd) -> {
                return updateDo(cmd);
            });
        }
    }

    protected int updateDo(SqlCommand cmd) throws SQLException {
        try (StatementHolder holder = beginStatement(cmd, false, false)) {
            return holder.stmt.executeUpdate();
        }
    }

    @Override
    public <T> T updateReturnKey() throws SQLException {
        if (interceptor == null) {
            return (T) updateReturnKeyDo(command);
        } else {
            return (T) interceptor.doIntercept(command, (cmd) -> {
                return updateReturnKeyDo(cmd);
            });
        }
    }

    protected Object updateReturnKeyDo(SqlCommand cmd) throws SQLException {
        try (StatementHolder holder = beginStatement(cmd, true, false)) {
            holder.stmt.executeUpdate();
            holder.rsts = holder.stmt.getGeneratedKeys();

            if (holder.rsts.next()) {
                return holder.rsts.getObject(1);
            } else {
                return null;
            }
        }
    }

    @Override
    public int[] updateBatch() throws SQLException {
        if (interceptor == null) {
            return updateBatchDo(command);
        } else {
            return (int[]) interceptor.doIntercept(command, (cmd) -> {
                return updateBatchDo(cmd);
            });
        }
    }

    protected int[] updateBatchDo(SqlCommand cmd) throws SQLException {
        try (StatementHolder holder = beginStatement(cmd, false, false)) {
            return holder.stmt.executeBatch();
        }
    }

    @Override
    public <T> List<T> updateBatchReturnKeys() throws SQLException {
        if (interceptor == null) {
            return updateBatchReturnKeysDo(command);
        } else {
            return (List<T>) interceptor.doIntercept(command, (cmd) -> {
                return updateBatchReturnKeysDo(cmd);
            });
        }
    }

    protected <T> List<T> updateBatchReturnKeysDo(SqlCommand cmd) throws SQLException {
        try (StatementHolder holder = beginStatement(cmd, true, false)) {
            holder.stmt.executeBatch();
            holder.rsts = holder.stmt.getGeneratedKeys();

            List<T> keyList = new ArrayList<>();
            while (holder.rsts.next()) {
                keyList.add((T) holder.rsts.getObject(1));
            }

            return keyList;
        }
    }

    /////////////////////

    /**
     * 开始命令处理
     */
    protected StatementHolder beginStatement(SqlCommand cmd, boolean returnKeys, boolean isStream) throws SQLException {
        StatementHolder holder = new StatementHolder();
        holder.conn = getConnection();

        if (isStream) {
            //流
            if (cmd.getSql().startsWith("{call")) {
                holder.stmt = holder.conn.prepareCall(cmd.getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } else {
                holder.stmt = holder.conn.prepareStatement(cmd.getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }
        } else {
            if (returnKeys) {
                //插入
                holder.stmt = holder.conn.prepareStatement(cmd.getSql(), Statement.RETURN_GENERATED_KEYS);
            } else {
                //其它
                if (cmd.getSql().startsWith("{call")) {
                    holder.stmt = holder.conn.prepareCall(cmd.getSql());
                } else {
                    holder.stmt = holder.conn.prepareStatement(cmd.getSql());
                }
            }
        }

        //绑定参数
        cmd.fill(holder.stmt);

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