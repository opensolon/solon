package org.hibernate.solon.jap.impl;

import org.hibernate.SessionFactory;
import org.noear.data.jap.JpaRepository;

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
    public void deleteInBatch(Iterable<Object> entities) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Object getOne(Object o) {
        return null;
    }
}
