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
package org.noear.solon.data.rx.sql;

import org.noear.solon.data.rx.sql.bound.RxRowConverter;
import org.noear.solon.data.rx.sql.bound.RxStatementBinder;
import org.noear.solon.lang.Nullable;
import org.noear.solon.lang.Preview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Sql 执行器
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
public interface RxSqlExecutor {
    /**
     * 查询并获取值
     *
     * @return 值
     */
    @Nullable
    <T> Mono<T> queryValue(Class<T> tClass);

    /**
     * 查询并获取值列表
     *
     * @return 值列表
     */
    @Nullable
    <T> Flux<T> queryValueList(Class<T> tClass);

    /**
     * 查询并获取行
     *
     * @param tClass Map.class or T.class
     * @return 值
     */
    @Nullable
    <T> Mono<T> queryRow(Class<T> tClass);

    /**
     * 查询并获取行
     *
     * @return 值
     */
    @Nullable
    <T> Mono<T> queryRow(RxRowConverter<T> converter);

    /**
     * 查询并获取行列表
     *
     * @param tClass Map.class or T.class
     * @return 值列表
     */
    @Nullable
    <T> Flux<T> queryRowList(Class<T> tClass);

    /**
     * 查询并获取行列表
     *
     * @return 值列表
     */
    @Nullable
    <T> Flux<T> queryRowList(RxRowConverter<T> converter);

    /**
     * 更新（插入、或更新、或删除）
     *
     * @return 受影响行数
     */
    Mono<Long> update();

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param args   参数
     * @param binder 绑定器
     * @return 受影响行数组
     */
    <S> Mono<Long> update(S args, RxStatementBinder<S> binder);

    /**
     * 更新并返回主键
     *
     * @return 主键
     */
    @Nullable
    <T> Mono<T> updateReturnKey(Class<T> tClass);

    /**
     * 更新并返回主键
     *
     * @return 主键
     */
    @Nullable
    <T, S> Mono<T> updateReturnKey(Class<T> tClass, S args, RxStatementBinder<S> binder);

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param argsList 参数集合
     * @return 受影响行数组
     */
    Flux<Long> updateBatch(Collection<Object[]> argsList);

    /**
     * 批量更新（插入、或更新、或删除）
     *
     * @param argsList 参数集合
     * @param binder   绑定器
     * @return 受影响行数组
     */
    <S> Flux<Long> updateBatch(Collection<S> argsList, RxStatementBinder<S> binder);
}