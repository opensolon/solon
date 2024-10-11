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
    <T> T queryValue(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取数组
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    <T> List<T> queryValueArray(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取行
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    Row queryRow(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取行列表
     *
     * @param sql SQL for retrieving record
     */
    @Nullable
    RowList queryRowList(String sql, Object... args) throws SQLException;

    /**
     * 查询并获取行遍历器（流式读取）
     *
     * @param sql SQL for retrieving record
     */
    RowIterator queryRowIterator(String sql, int fetchSize, Object... args) throws SQLException;


    /**
     * 更新（插入、或更新、或删除）
     *
     * @param sql SQL for retrieving record
     * @return 受影响行数
     */
    int update(String sql, Object... args) throws SQLException;

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param sql SQL for retrieving record
     * @return 受影响行数组
     */
    int[] updateBatch(String sql, Collection<Object[]> argsList) throws SQLException;


    /**
     * 更新并返回主键
     *
     * @param sql SQL for retrieving record
     * @return 自增键
     */
    long updateReturnKey(String sql, Object... args) throws SQLException;
}