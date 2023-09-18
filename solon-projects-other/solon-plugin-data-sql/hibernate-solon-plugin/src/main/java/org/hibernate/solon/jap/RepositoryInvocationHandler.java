package org.hibernate.solon.jap;

import org.hibernate.SessionFactory;
import org.hibernate.solon.jap.impl.JapRepositoryProxyImpl;
import org.noear.data.jap.CrudRepository;
import org.noear.data.jap.JpaRepository;
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
        if (CrudRepository.class == method.getDeclaringClass()
                || PagingAndSortingRepository.class == method.getDeclaringClass()
                || JpaRepository.class == method.getDeclaringClass()) {
            //归属 JpaRepository 范围的
            JapRepositoryProxyImpl repository = new JapRepositoryProxyImpl(sessionFactory, repositoryInterface);
            return method.invoke(repository, args);
        } else {
            //无归属的 method 处理
        }

        return null;
    }
}
