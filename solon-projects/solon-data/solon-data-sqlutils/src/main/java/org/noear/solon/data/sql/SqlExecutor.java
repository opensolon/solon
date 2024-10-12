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

import org.noear.solon.lang.Nullable;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Sql 执行器
 *
 * @author noear
 * @since 3.0
 */
public interface SqlExecutor {
    /**
     * 查询并获取值
     *
     * @return 值
     */
    @Nullable
    <T> T queryValue() throws SQLException;

    /**
     * 查询并获取值列表
     *
     * @return 值列表
     */
    @Nullable
    <T> List<T> queryValueList() throws SQLException;

    /**
     * 查询并获取行
     *
     * @return 行
     */
    @Nullable
    Row queryRow() throws SQLException;

    /**
     * 查询并获取行列表
     *
     * @return 行列表
     */
    @Nullable
    RowList queryRowList() throws SQLException;

    /**
     * 查询并获取行遍历器（流式读取）
     *
     * @return 行遍历器
     */
    RowIterator queryRowIterator(int fetchSize) throws SQLException;


    /**
     * 更新（插入、或更新、或删除）
     *
     * @return 受影响行数
     */
    int update() throws SQLException;

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @return 受影响行数组
     */
    int[] updateBatch(Collection<Object[]> argsList) throws SQLException;


    /**
     * 更新并返回主键
     *
     * @return 主键
     */
    long updateReturnKey() throws SQLException;
}