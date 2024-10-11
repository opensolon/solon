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
import org.noear.solon.data.sql.RowIterator;
import org.noear.solon.data.sql.RowList;
import org.noear.solon.data.sql.SqlExecutor;

import java.sql.SQLException;
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
    private final SimpleSqlUtils utils;
    private final String sql;
    private final Object[] args;

    public SimpleSqlExecutor(SimpleSqlUtils utils, String sql, Object[] args) {
        this.utils = utils;
        this.sql = sql;
        this.args = args;
    }

    @Override
    public <T> T queryValue() throws SQLException {
        try (CommandHolder holder = utils.buildCommand(sql, args, false, false)) {
            holder.rsts = holder.stmt.executeQuery();

            if (holder.rsts.next()) {
                return (T) utils.getObject(holder, 1);
            }

            return null;
        }
    }

    @Override
    public <T> List<T> queryValueList() throws SQLException {
        try (CommandHolder holder = utils.buildCommand(sql, args, false, false)) {
            holder.rsts = holder.stmt.executeQuery();

            List<T> list = new ArrayList<>();

            while (holder.rsts.next()) {
                list.add((T) utils.getObject(holder, 1));
            }

            return list.size() > 0 ? list : null;
        }
    }

    @Override
    public Row queryRow() throws SQLException {
        try (CommandHolder holder = utils.buildCommand(sql, args, false, false)) {
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
        try (CommandHolder holder = utils.buildCommand(sql, args, false, false)) {
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
        CommandHolder holder = utils.buildCommand(sql, args, false, true);
        holder.stmt.setFetchSize(fetchSize);
        holder.rsts = holder.stmt.executeQuery();

        return new SimpleRowIterator(holder);
    }

    @Override
    public int update() throws SQLException {
        try (CommandHolder holder = utils.buildCommand(sql, args, false, false)) {
            return holder.stmt.executeUpdate();
        }
    }

    @Override
    public int[] updateBatch(Collection<Object[]> argsList) throws SQLException {
        try (CommandHolder holder = utils.buildCommand(sql, argsList, false, false)) {
            return holder.stmt.executeBatch();
        }
    }

    @Override
    public long updateReturnKey() throws SQLException {
        try (CommandHolder holder = utils.buildCommand(sql, args, true, false)) {
            holder.stmt.executeUpdate();
            holder.rsts = holder.stmt.getGeneratedKeys();

            if (holder.rsts.next()) {
                return holder.rsts.getLong(1);
            } else {
                return -1L;
            }
        }
    }
}
