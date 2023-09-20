package org.noear.data.jpa;

import java.util.Optional;
import java.util.function.Function;

public interface QueryByExampleExecutor<T> {
    <S extends T> Optional<S> findOne(Example<S> example);

    <S extends T> Iterable<S> findAll(Example<S> example);

    <S extends T> Iterable<S> findAll(Example<S> example, Sort sort);

    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends T> long count(Example<S> example);

    <S extends T> boolean exists(Example<S> example);

    <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
