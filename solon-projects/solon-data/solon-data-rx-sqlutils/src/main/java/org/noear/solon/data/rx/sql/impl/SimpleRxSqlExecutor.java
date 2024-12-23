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
package org.noear.solon.data.rx.sql.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Statement;
import org.noear.solon.data.rx.sql.RxSqlExecutor;
import org.noear.solon.data.rx.sql.bound.RxRowConverter;
import org.noear.solon.data.rx.sql.bound.RxStatementBinder;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * Sql 执行器简单实现
 *
 * @author noear
 * @since 3.0
 */
public class SimpleRxSqlExecutor implements RxSqlExecutor {
    private final ConnectionFactory dataSource;
    private final String sql;
    private final Object[] argsDef;
    private final static DefaultRxBinder binderDef = new DefaultRxBinder();

    public SimpleRxSqlExecutor(ConnectionFactory dataSource, String sql, Object[] args) {
        this.dataSource = dataSource;
        this.sql = sql;
        this.argsDef = args;
    }

    @Override
    public <T> Mono<T> queryValue(Class<T> tClass) {
        return (Mono<T>) queryRow((row, rowM) -> row.get(0));
    }

    @Override
    public <T> Flux<T> queryValueList(Class<T> tClass) {
        return (Flux<T>) queryRowList((row, rowM) -> row.get(0));
    }

    @Override
    public <T> Mono<T> queryRow(Class<T> tClass) {
        return queryRow((RxRowConverter<T>) DefaultRxConverter.getInstance().create(tClass));
    }

    @Override
    public <T> Mono<T> queryRow(RxRowConverter<T> converter) {
        return Mono.from(getConnection())
                .flatMapMany(conn -> binderDef.setValues(conn.createStatement(sql), argsDef).execute())
                .flatMap(result -> result.map(converter::convert))
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public <T> Flux<T> queryRowList(Class<T> tClass) {
        return queryRowList(DefaultRxConverter.getInstance().create(tClass));
    }

    @Override
    public <T> Flux<T> queryRowList(RxRowConverter<T> converter) {
        return Mono.from(getConnection())
                .flatMapMany(conn -> binderDef.setValues(conn.createStatement(sql), argsDef).execute())
                .flatMap(result -> result.map(converter::convert));
    }

    @Override
    public Mono<Long> update() {
        return update(argsDef, binderDef);
    }

    @Override
    public <S> Mono<Long> update(S args, RxStatementBinder<S> binder) {
        return Mono.from(getConnection())
                .flatMapMany(conn -> binder.setValues(conn.createStatement(sql), args).execute())
                .flatMap(result -> result.getRowsUpdated())
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public <T> Mono<T> updateReturnKey(Class<T> tClass) {
        return updateReturnKey(tClass, argsDef, binderDef);
    }

    @Override
    public <T, S> Mono<T> updateReturnKey(Class<T> tClass, S args, RxStatementBinder<S> binder) {
        return (Mono<T>) Mono.from(getConnection())
                .flatMapMany(conn -> binder.setValues(conn.createStatement(sql).returnGeneratedValues(), args).execute())
                .flatMap(result -> result.map(r -> r.get(0)))
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public Flux<Long> updateBatch(Collection<Object[]> argsList) {
        return updateBatch(argsList, binderDef);
    }

    @Override
    public <T> Flux<Long> updateBatch(Collection<T> argsList, RxStatementBinder<T> binder) {
        return Mono.from(getConnection())
                .flatMapMany(conn -> {
                    Statement stmt = conn.createStatement(sql);
                    int count = 0;
                    for (T row : argsList) {
                        if (count > 0) {
                            stmt.add();
                        }

                        binder.setValues(stmt, row);
                        count++;
                    }
                    return stmt.execute();
                })
                .flatMap(result -> result.getRowsUpdated());
    }


    /////////////////////


    /**
     * 获取连接（为转换提供重写机会）
     */
    protected Publisher<? extends Connection> getConnection() {
        return dataSource.create();

//        if (Solon.app() == null) {
//            return dataSource.create();
//        } else {
//            return null;
//            //return TranUtils.getConnectionProxy(dataSource);
//        }
    }
}