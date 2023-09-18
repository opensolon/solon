package org.hibernate.solon.jap.impl;

import org.hibernate.SessionFactory;
import org.noear.data.jap.Page;
import org.noear.data.jap.Pageable;
import org.noear.data.jap.PagingAndSortingRepository;
import org.noear.data.jap.Sort;


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
