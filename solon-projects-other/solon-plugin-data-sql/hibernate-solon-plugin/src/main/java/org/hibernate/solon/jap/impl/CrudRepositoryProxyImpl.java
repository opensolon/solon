package org.hibernate.solon.jap.impl;

import org.hibernate.SessionFactory;
import org.noear.data.jap.CrudRepository;
import org.noear.solon.core.util.GenericUtil;

import java.util.Optional;

/**
 * @author noear
 * @since 2.5
 */
public class CrudRepositoryProxyImpl extends BaseRepositoryProxyImpl implements CrudRepository<Object,Object> {
    protected Class<?> entityType;
    protected Class<?> idType;

    public CrudRepositoryProxyImpl(SessionFactory sessionFactory, Class<?> repositoryInterface) {
        super(sessionFactory);

        Class<?>[] typeArguments = GenericUtil.resolveTypeArguments(repositoryInterface, CrudRepository.class);

        this.entityType = typeArguments[0];
        this.idType = typeArguments[1];
    }

    @Override
    public Object save(Object entity) {
        return null;
    }

    @Override
    public Iterable saveAll(Iterable entities) {
        return null;
    }

    @Override
    public Optional findById(Object o) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Object o) {
        return false;
    }

    @Override
    public Iterable findAll() {
        return null;
    }

    @Override
    public Iterable findAllById(Iterable iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Object o) {

    }

    @Override
    public void delete(Object entity) {

    }

    @Override
    public void deleteAll(Iterable entities) {

    }

    @Override
    public void deleteAll() {

    }
}
