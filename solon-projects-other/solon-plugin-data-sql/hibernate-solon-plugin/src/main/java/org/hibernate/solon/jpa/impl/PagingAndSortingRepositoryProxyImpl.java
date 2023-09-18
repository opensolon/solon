package org.hibernate.solon.jpa.impl;

import org.hibernate.SessionFactory;
import org.noear.data.jpa.Page;
import org.noear.data.jpa.Pageable;
import org.noear.data.jpa.PagingAndSortingRepository;
import org.noear.data.jpa.Sort;


/**
 * @author noear
 * @since 2.5
 */
public class PagingAndSortingRepositoryProxyImpl extends CrudRepositoryProxyImpl implements PagingAndSortingRepository<Object,Object> {
    public PagingAndSortingRepositoryProxyImpl(SessionFactory sessionFactory, Class<?> repositoryInterface) {
        super(sessionFactory, repositoryInterface);
    }

    @Override
    public Iterable<Object> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Object> findAll(Pageable pageable) {
        return null;
    }
}
