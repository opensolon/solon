package org.noear.solon.extend.mybatis;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.*;
import org.noear.solon.ext.ConsumerEx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理
 * */
public class MybatisProxy extends SqlSessionHolder implements SqlSession {
    private final SqlSessionFactory factory;

    private final static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    private final static Map<SqlSessionFactory, MybatisProxy> cached = new ConcurrentHashMap<>();

    /**
     * 获取代理
     * */
    public static MybatisProxy get(SqlSessionFactory factory) {
        MybatisProxy wrap = cached.get(factory);
        if (wrap == null) {
            synchronized (cached) {
                wrap = cached.get(factory);
                if (wrap == null) {
                    wrap = new MybatisProxy(factory);
                    cached.put(factory, wrap);
                }
            }
        }

        return wrap;
    }

    /**
     * 事务
     * */
    public Object tran(ConsumerEx<SqlSessionHolder> consumer) throws SQLException {
        SqlSession session = null;
        try {
            session = factory.openSession();
            threadLocal.set(session);

            SqlSessionHolder session2 = new SqlSessionHolder(session);
            consumer.accept(session2);

            session.commit();

            return session2.result;
        } catch (Throwable ex) {
            if (session != null) {
                session.rollback();
            }

            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else if (ex instanceof SQLException) {
                throw (SQLException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        } finally {
            threadLocal.remove();

            if (session != null) {
                session.close();
            }
        }
    }

    protected MybatisProxy(SqlSessionFactory factory) {
        super((SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor(factory)));
        this.factory = factory;
    }

    protected static class SqlSessionInterceptor implements InvocationHandler {
        private SqlSessionFactory factory;

        public SqlSessionInterceptor(SqlSessionFactory factory) {
            this.factory = factory;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession session = MybatisProxy.threadLocal.get();
            Boolean has_close = false;
            if (session == null) {
                has_close = true;
                session = factory.openSession(true); //isAutoCommit
            }

            Object unwrapped = null;

            try {
                Object result = method.invoke(session, args);
                unwrapped = result;
            } catch (Throwable ex) {
                unwrapped = ExceptionUtil.unwrapThrowable(ex);
                throw (Throwable) unwrapped;
            } finally {
                if (session != null && has_close) {
                    session.close();
                }
            }

            return unwrapped;
        }
    }
}
