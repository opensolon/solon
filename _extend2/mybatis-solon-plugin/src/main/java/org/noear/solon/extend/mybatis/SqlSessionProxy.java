package org.noear.solon.extend.mybatis;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.*;
import org.noear.solon.ext.RunnableEx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlSessionProxy implements SqlSession {
    private final SqlSessionFactory factory;
    private final SqlSession proxy;

    private final static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    private final static Map<SqlSessionFactory, SqlSessionProxy> cached = new ConcurrentHashMap<>();

    public static SqlSessionProxy get(SqlSessionFactory factory) {
        SqlSessionProxy wrap = cached.get(factory);
        if (wrap == null) {
            wrap = new SqlSessionProxy(factory);
            cached.putIfAbsent(factory, wrap);
        }

        return wrap;
    }

    public void tran(RunnableEx runnable) throws SQLException {
        SqlSession session = null;

        try {
            session = factory.openSession();
            threadLocal.set(session);

            runnable.run();

            session.commit();
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

    protected SqlSessionProxy(SqlSessionFactory factory) {
        this.factory = factory;
        this.proxy = (SqlSession) Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionProxy.SqlSessionInterceptor());
    }

    @Override
    public <T> T selectOne(String s) {
        return proxy.selectOne(s);
    }

    @Override
    public <T> T selectOne(String s, Object o) {
        return proxy.selectOne(s, o);
    }

    @Override
    public <E> List<E> selectList(String s) {
        return proxy.selectList(s);
    }

    @Override
    public <E> List<E> selectList(String s, Object o) {
        return proxy.selectList(s, o);
    }

    @Override
    public <E> List<E> selectList(String s, Object o, RowBounds rowBounds) {
        return proxy.selectList(s, o, rowBounds);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, String s1) {
        return proxy.selectMap(s, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1) {
        return proxy.selectMap(s, o, s1);
    }

    @Override
    public <K, V> Map<K, V> selectMap(String s, Object o, String s1, RowBounds rowBounds) {
        return proxy.selectMap(s, o, s1, rowBounds);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s) {
        return proxy.selectCursor(s);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o) {
        return proxy.selectCursor(s, o);
    }

    @Override
    public <T> Cursor<T> selectCursor(String s, Object o, RowBounds rowBounds) {
        return proxy.selectCursor(s, o, rowBounds);
    }

    @Override
    public void select(String s, Object o, ResultHandler resultHandler) {
        proxy.select(s, o, resultHandler);
    }

    @Override
    public void select(String s, ResultHandler resultHandler) {
        proxy.select(s, resultHandler);
    }

    @Override
    public void select(String s, Object o, RowBounds rowBounds, ResultHandler resultHandler) {
        proxy.select(s, o, rowBounds, resultHandler);
    }

    @Override
    public int insert(String s) {
        return proxy.insert(s);
    }

    @Override
    public int insert(String s, Object o) {
        return proxy.insert(s, 0);
    }

    @Override
    public int update(String s) {
        return proxy.update(s);
    }

    @Override
    public int update(String s, Object o) {
        return proxy.update(s, o);
    }

    @Override
    public int delete(String s) {
        return proxy.delete(s);
    }

    @Override
    public int delete(String s, Object o) {
        return proxy.delete(s, o);
    }

    @Override
    public List<BatchResult> flushStatements() {
        return proxy.flushStatements();
    }


    @Override
    public void clearCache() {
        proxy.clearCache();
    }

    @Override
    public Configuration getConfiguration() {
        return proxy.getConfiguration();
    }

    @Override
    public <T> T getMapper(Class<T> aClass) {
        return getConfiguration().getMapper(aClass, this);
    }

    @Override
    public Connection getConnection() {
        return proxy.getConnection();
    }

    public void commit() {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Solon managed SqlSession");
    }

    public void commit(boolean force) {
        throw new UnsupportedOperationException("Manual commit is not allowed over a Solon managed SqlSession");
    }

    public void rollback() {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Solon managed SqlSession");
    }

    public void rollback(boolean force) {
        throw new UnsupportedOperationException("Manual rollback is not allowed over a Solon managed SqlSession");
    }

    public void close() {
        throw new UnsupportedOperationException("Manual close is not allowed over a Solon managed SqlSession");
    }


    private class SqlSessionInterceptor implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            SqlSession session = SqlSessionProxy.threadLocal.get();
            Boolean has_close = false;
            if(session == null){
                has_close = true;
                session = SqlSessionProxy.this.factory.openSession(true); //isAutoCommit
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
