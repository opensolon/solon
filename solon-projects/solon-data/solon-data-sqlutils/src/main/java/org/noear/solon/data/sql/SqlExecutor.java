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

import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowIterator;
import org.noear.solon.data.sql.bound.StatementBinder;
import org.noear.solon.lang.Nullable;
import org.noear.solon.lang.Preview;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Sql 执行器
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
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
     * @param tClass Map.class or T.class
     * @return 值
     */
    @Nullable
    <T> T queryRow(Class<T> tClass) throws SQLException;

    /**
     * 查询并获取行
     *
     * @return 值
     */
    @Nullable
    <T> T queryRow(RowConverter<T> converter) throws SQLException;

    /**
     * 查询并获取行列表
     *
     * @param tClass Map.class or T.class
     * @return 值列表
     */
    @Nullable
    <T> List<T> queryRowList(Class<T> tClass) throws SQLException;

    /**
     * 查询并获取行列表
     *
     * @return 值列表
     */
    @Nullable
    <T> List<T> queryRowList(RowConverter<T> converter) throws SQLException;

    /**
     * 查询并获取行遍历器（流式读取）
     *
     * @param tClass Map.class or T.class
     * @return 行遍历器
     */
    <T> RowIterator<T> queryRowIterator(int fetchSize, Class<T> tClass) throws SQLException;

    /**
     * 查询并获取行遍历器（流式读取）
     *
     * @return 行遍历器
     */
    <T> RowIterator<T> queryRowIterator(int fetchSize, RowConverter<T> converter) throws SQLException;

    /// //////////////////////

    /**
     * 更新（插入、或更新、或删除）
     *
     * @return 受影响行数
     */
    int update() throws SQLException;

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param args   参数
     * @param binder 绑定器
     * @return 受影响行数组
     */
    <S> int update(S args, StatementBinder<S> binder) throws SQLException;


    /**
     * 更新并返回主键
     *
     * @return 主键
     */
    @Nullable
    <T> T updateReturnKey() throws SQLException;

    /**
     * 更新并返回主键
     *
     * @return 主键
     */
    @Nullable
    <T, S> T updateReturnKey(S args, StatementBinder<S> binder) throws SQLException;

    /// //////////////////////

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param argsList 参数集合
     * @return 受影响行数组
     */
    int[] updateBatch(Collection<Object[]> argsList) throws SQLException;

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param argsList 参数集合
     * @param binder   绑定器
     * @return 受影响行数组
     */
    <S> int[] updateBatch(Collection<S> argsList, StatementBinder<S> binder) throws SQLException;

    /**
     * 批量更新并返回主键（插入、或更新、或删除）
     *
     * @param argsList 参数集合
     * @return 受影响行数组
     */
    <T> List<T> updateBatchReturnKey(Collection<Object[]> argsList) throws SQLException;


    /**
     * 批量更新并返回主键（插入、或更新、或删除）
     *
     * @param argsList 参数集合
     * @param binder   绑定器
     * @return 受影响行数组
     */
    <T, S> List<T> updateBatchReturnKey(Collection<S> argsList, StatementBinder<S> binder) throws SQLException;
}