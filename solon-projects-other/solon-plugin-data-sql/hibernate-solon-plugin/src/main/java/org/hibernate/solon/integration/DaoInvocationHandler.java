package org.hibernate.solon.integration;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.solon.Dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DaoInvocationHandler implements InvocationHandler {
    private SessionFactory sessionFactory;
    private Class<?> entityClass;
    private Class<?> daoInterface;

    public DaoInvocationHandler(SessionFactory sessionFactory, Class<?> entityClass, Class<?> daoInterface) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
        this.daoInterface = daoInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Dao.class) {
            if ("saveOrUpdate".equals(method.getName())) {
                Session session = session();
                session.persist(args[0]);// 保存或更新
            }
        }
        return null;
    }


    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}