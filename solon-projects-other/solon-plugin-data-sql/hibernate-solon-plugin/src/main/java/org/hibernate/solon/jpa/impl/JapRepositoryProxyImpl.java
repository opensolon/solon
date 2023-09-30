package org.hibernate.solon.jpa.impl;

import org.hibernate.SessionFactory;
import org.noear.data.jpa.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author noear
 * @since 2.5
 */
public class JapRepositoryProxyImpl extends PagingAndSortingRepositoryProxyImpl implements JpaRepository<Object,Object> {
    public JapRepositoryProxyImpl(SessionFactory sessionFactory, Class<?> repositoryInterface) {
        super(sessionFactory, repositoryInterface);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Object> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Object> objects) {

    }


    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Object getReferenceById(Object o) {
        return null;
    }

    @Override
    public <S> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
