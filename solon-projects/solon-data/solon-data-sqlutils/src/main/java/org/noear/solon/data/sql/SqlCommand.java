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
package org.noear.solon.data.sql;

import org.noear.solon.data.sql.bound.StatementBinder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Sql 命令
 *
 * @author noear
 * @since 3.1
 */
public class SqlCommand<T> {
    private final DataSource ds;
    private StatementBinder<T> binder;
    private String sql;
    private T args;
    private Collection<T> argsColl;

    public SqlCommand(DataSource ds, String sql, T args, StatementBinder<T> binder) {
        this.ds = ds;
        this.sql = sql;
        this.args = args;
        this.argsColl = null;
        this.binder = binder;
    }

    public SqlCommand(DataSource ds, String sql, Collection<T> argsColl, StatementBinder<T> binder) {
        this.ds = ds;
        this.sql = sql;
        this.args = null;
        this.argsColl = argsColl;
        this.binder = binder;
    }

    /**
     * 是否为批处理
     */
    public boolean isBatch() {
        return argsColl != null;
    }

    /**
     * 获取数据源
     */
    public DataSource getDs() {
        return ds;
    }

    /**
     * 设置查询语句
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 获取查询语句
     */
    public String getSql() {
        return sql;
    }

    /**
     * 设置参数
     */
    public void setArgs(T args) {
        this.argsColl = null;
        this.args = args;
    }

    /**
     * 获取参数
     */
    public T getArgs() {
        return args;
    }

    /**
     * 设置参数集合（用于批处理场景）
     */
    public void setArgsColl(Collection<T> argsColl) {
        this.argsColl = argsColl;
        this.args = null;
    }

    /**
     * 获取参数集合（用于批处理场景）
     */
    public Collection<T> getArgsColl() {
        return argsColl;
    }

    /**
     * 获取绑定器
     */
    public StatementBinder<T> getBinder() {
        return binder;
    }

    /**
     * 填充
     */
    public void fill(PreparedStatement stmt) throws SQLException {
        if (argsColl == null) {
            binder.setValues(stmt, args);
        } else {
            for (T row : argsColl) {
                binder.setValues(stmt, row);
                stmt.addBatch();
            }
        }
    }

    @Override
    public String toString() {
        return sql;
    }
}