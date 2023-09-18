package org.hibernate.solon.jap;

import org.hibernate.SessionFactory;
import org.hibernate.solon.jap.impl.CrudRepositoryProxyImpl;
import org.hibernate.solon.jap.impl.PagingAndSortingRepositoryProxyImpl;
import org.noear.data.jap.CrudRepository;
import org.noear.data.jap.PagingAndSortingRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class RepositoryInvocationHandler implements InvocationHandler {
    private final SessionFactory sessionFactory;
    private final Class<?> repositoryInterface;

    public RepositoryInvocationHandler(SessionFactory sessionFactory, Class<?> repositoryInterface) {
        this.sessionFactory = sessionFactory;
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (CrudRepository.class == method.getDeclaringClass()) {
            CrudRepositoryProxyImpl repository = new CrudRepositoryProxyImpl(sessionFactory, repositoryInterface);
            method.invoke(repository, args);
        } else if (PagingAndSortingRepository.class == method.getDeclaringClass()) {
            PagingAndSortingRepository repository = new PagingAndSortingRepositoryProxyImpl(sessionFactory, repositoryInterface);
            method.invoke(repository, args);
        }

        //...

        return null;
    }
}
