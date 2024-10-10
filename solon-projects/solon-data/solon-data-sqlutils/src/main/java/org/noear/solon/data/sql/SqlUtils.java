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
package org.noear.solon.data.sql;

import org.noear.solon.data.sql.impl.SimpleSqlUtils;
import org.noear.solon.lang.Nullable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Sql 工具类（线程安全，可作为单例保存）
 *
 * @author noear
 * @since 3.0
 */
public interface SqlUtils {
    static SqlUtils of(DataSource dataSource) {
        return new SimpleSqlUtils(dataSource);
    }

    /**
     * 查询并获取单值
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    <T> T selectValue(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取数组
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    <T> List<T> selectValueArray(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取行
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    Row selectRow(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取行列表
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    List<Row> selectRowList(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取行遍历器
     *
     * @param sql SQL for retrieving record
     */
    Iterator<Row> selectRowStream(String sql, int fetchSize, Object... args) throws SQLException;

    /**
     * 插入
     *
     * @param sql SQL for retrieving record
     * @return 受影响行数
     */
    int insert(String sql, Object... args) throws SQLException;

    /**
     * 插入并返回自增键
     *
     * @param sql SQL for retrieving record
     * @return 自增键
     */
    long insertReturnKey(String sql, Object... args) throws SQLException;

    /**
     * 执行（更新，或删除）
     *
     * @param sql SQL for retrieving record
     * @return 受影响行数
     */
    int execute(String sql, Object... args) throws SQLException;

    /**
     * 批量执行（更新，或删除）
     *
     * @param sql SQL for retrieving record
     * @return 受影响行数组
     */
    int[] executeBatch(String sql, Collection<Object[]> argsList) throws SQLException;
}