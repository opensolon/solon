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
package org.noear.solon.data.rx.sql.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.noear.solon.data.rx.sql.RxSqlCommand;
import org.noear.solon.data.rx.sql.RxSqlExecutor;
import org.noear.solon.data.rx.sql.bound.RxRowConverter;
import org.noear.solon.data.rx.sql.bound.RxStatementBinder;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Sql 执行器简单实现
 *
 * @author noear
 * @since 3.0
 */
public class SimpleRxSqlExecutor implements RxSqlExecutor {
    private final static DefaultRxBinder DEFAULT_BINDER = new DefaultRxBinder();
    private final ConnectionFactory dataSource;
    private final String commandText;
    private RxSqlCommand command;
    private Consumer<RxSqlCommand> onCommandPost;

    public SimpleRxSqlExecutor(ConnectionFactory dataSource, String sql) {
        this.dataSource = dataSource;
        this.commandText = sql;
    }

    @Override
    public RxSqlExecutor params(Object... args) {
        this.command = new RxSqlCommand(commandText, args, DEFAULT_BINDER);
        return this;
    }

    @Override
    public <S> RxSqlExecutor params(S args, RxStatementBinder<S> binder) {
        this.command = new RxSqlCommand(commandText, args, binder);
        return this;
    }

    @Override
    public RxSqlExecutor params(Collection<Object[]> argsList) {
        this.command = new RxSqlCommand(commandText, argsList, DEFAULT_BINDER);
        return this;
    }

    @Override
    public <S> RxSqlExecutor params(Collection<S> argsList, Supplier<RxStatementBinder<S>> binderSupplier) {
        this.command = new RxSqlCommand(commandText, argsList, binderSupplier.get());
        return this;
    }

    public RxSqlExecutor onCommandPost(Consumer<RxSqlCommand> action) {
        this.onCommandPost = action;
        return this;
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
                .flatMapMany(conn -> buildStatement(conn, command, false))
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
                .flatMapMany(conn -> buildStatement(conn, command, false))
                .flatMap(result -> result.map(converter::convert));
    }

    @Override
    public Mono<Long> update() {
        return Mono.from(getConnection())
                .flatMapMany(conn -> buildStatement(conn, command, false))
                .flatMap(result -> result.getRowsUpdated())
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public <T> Mono<T> updateReturnKey(Class<T> tClass) {
        return (Mono<T>) Mono.from(getConnection())
                .flatMapMany(conn -> buildStatement(conn, command, true))
                .flatMap(result -> result.map(r -> r.get(0)))
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public Flux<Long> updateBatch() {
        return Mono.from(getConnection())
                .flatMapMany(conn -> buildStatement(conn, command, false))
                .flatMap(result -> result.getRowsUpdated());
    }


    /// //////////////////

    protected Publisher<? extends Result> buildStatement(Connection conn, RxSqlCommand command, boolean returnKeys) {
        if (onCommandPost != null) {
            onCommandPost.accept(command);
        }

        Statement stmt = conn.createStatement(command.getSql());

        if (returnKeys) {
            stmt.returnGeneratedValues();
        }

        command.fill(stmt);
        return stmt.execute();
    }


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